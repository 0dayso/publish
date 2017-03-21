package com.shinemo.publish.websocket;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.CMDUtils;

public class ShellSocketHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(
            WebSocketServerHandshaker.class);

    private static ExecutorService executorService = new ThreadPoolExecutor(5,
			10, 20, TimeUnit.MINUTES, new LinkedBlockingQueue(10));
    
    private ShellSocketAuthService shellSocketAuthService;
    @Resource
    private ShellService shellService;

    private WebSocketServerHandshaker handshaker;

    public ShellSocketHandler(ShellSocketAuthService shellSocketAuthService,
    		ShellService shellService) {
        this.shellSocketAuthService = shellSocketAuthService;
        this.shellService = shellService;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("WEB_SOCKET_CHANNEL_ACTIVE,channelId:{}", ctx.channel().id().asLongText());
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("WEB_SOCKET_CHANNEL_INACTIVE,channelId:{}", ctx.channel().id().asLongText());
        ShellSocketChannelGroup.removeChannel(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        if (msg instanceof FullHttpRequest) {
            handleHandShake(ctx, ((FullHttpRequest) msg));
        } else if (msg instanceof WebSocketFrame) {
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("NETTY EXCEPTION,channelId:{}", ctx.channel().id().asLongText(), cause);
    }

    private void handlerWebSocketFrame(final ChannelHandlerContext ctx,
                                       WebSocketFrame frame) {
        // 1、check the request is close then channel cmd
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            logger.error("CLOSE HAND");
        }
        // 2、ignore ping msg
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 3、ignore UN TextWebSocketFrame MSG
        if (!(frame instanceof TextWebSocketFrame)) {
            logger.error("NOT SUPPORT BINARY BYTES MSG");
            return;
        }
        // 4、handler biz msg
        String originMsg = ((TextWebSocketFrame) frame).text();
        logger.info("RECEIVE ORIGIN_MSG:{}", originMsg);
        ShellSocketMsg msg = new Gson().fromJson(originMsg, ShellSocketMsg.class);
        if (msg.isHeartBeat()) return;
        try {
        	String hostinfo = msg.getHostinfo();
            String[] hostArr = hostinfo.split(":");
            final String user = hostArr[0];
            final String hostname = hostArr[1];
            final int port = Integer.parseInt(hostArr[2]);
            final String shell = msg.getShell();
            if(!CMDUtils.isAllow(shell)){
            	ctx.channel().writeAndFlush(new TextWebSocketFrame(shell+" is not allowed!"));
            }else{
            	executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						shellService.execForSock(ctx.channel(), hostname, port, user, "", CMDUtils.parseCmd(shell));
					}
				});
            	
            }
		} catch (Exception e) {
			e.printStackTrace();
			ctx.channel().writeAndFlush(new TextWebSocketFrame(e.getMessage()));
		}
        
    }

    private void handleHandShake(ChannelHandlerContext ctx,
                                 FullHttpRequest request) {
        //1、web socket version check
        if (!request.decoderResult().isSuccess()
                || (!"websocket".equals(request.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        //2、 login auth check
        String uri = request.uri();
        String sid = getSid(uri);
        
        
//        TODO
        if (!shellSocketAuthService.loginCheck(sid)) {
            logger.error("HIVE SOCKET LOGIN CHECK FAIL,uri:{}", uri);
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
            return;
        }
        //3、create handshaker
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                ShellSocketServer.WEBSOCKET_URL, null, false);
        handshaker = wsFactory.newHandshaker(request);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            logger.error("CREATE handshaker fail");
            return;
        }
        handshaker.handshake(ctx.channel(), request);

        //5、add channel to group
        //HiveSocketChannelGroup.add(policyEnum, customerId, ctx.channel());
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, DefaultFullHttpResponse res) {
        // parse response code
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // if code != 200,then close connection
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private static String getSid(String uri) {
        Map<String, String> map = parseAttributes(uri);
        if (null == map) return null;
        return map.get("sid");
    }

    private static Map<String, String> parseAttributes(String uri) {
        try {
            if (StringUtils.isBlank(uri)) return null;
            String[] arr = uri.split("\\?")[1].split("&");
            Map<String, String> map = new HashMap<String, String>(arr.length);
            for (String kv : arr) {
                String[] s = kv.split("=");
                if (s.length != 2) continue;
                map.put(s[0], s[1]);
            }
            return map;
        } catch (Exception ex) {
        }
        return null;
    }
}