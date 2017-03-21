package com.shinemo.publish.debug.websocket;

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
import com.shinemo.publish.debug.websocket.msg.BreakPointMsg;
import com.shinemo.publish.debug.websocket.msg.DisConnectMsg;
import com.shinemo.publish.debug.websocket.msg.FieldMsg;
import com.shinemo.publish.debug.websocket.msg.IMsg;
import com.shinemo.publish.debug.websocket.msg.MsgEnum;
import com.shinemo.publish.debug.websocket.msg.StatMsg;
import com.shinemo.publish.debug.websocket.msg.StepMsg;
import com.shinemo.publish.service.DebugService;

public class DebugSocketHandler extends SimpleChannelInboundHandler<Object> {
	private static final Logger logger = LoggerFactory
			.getLogger(DebugSocketHandler.class);

	private static ExecutorService executorService = new ThreadPoolExecutor(5,
			10, 20, TimeUnit.MINUTES, new LinkedBlockingQueue(10));

	private DebugSocketAuthService debugSocketAuthService;
	@Resource
	private DebugService debugService;

	private WebSocketServerHandshaker handshaker;

	public DebugSocketHandler(DebugSocketAuthService debugSocketAuthService,
			DebugService debugService) {
		this.debugSocketAuthService = debugSocketAuthService;
		this.debugService = debugService;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("WEB_SOCKET_CHANNEL_ACTIVE,channelId:{}", ctx.channel()
				.id().asLongText());
		ctx.fireChannelActive();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("WEB_SOCKET_CHANNEL_INACTIVE,channelId:{}", ctx.channel()
				.id().asLongText());
		DebugSocketChannelGroup.removeChannel(ctx.channel());
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
		logger.error("NETTY EXCEPTION,channelId:{}", ctx.channel().id()
				.asLongText(), cause);
	}

	private void handlerWebSocketFrame(final ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		// 1、check the request is close then channel cmd
		if (frame instanceof CloseWebSocketFrame) {
			handshaker.close(ctx.channel(),
					(CloseWebSocketFrame) frame.retain());
			logger.info("CLOSE HAND");
		}
		// 2、ignore ping msg
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().write(
					new PongWebSocketFrame(frame.content().retain()));
			return;
		}
		// 3、ignore UN TextWebSocketFrame MSG
		if (!(frame instanceof TextWebSocketFrame)) {
			logger.error("NOT SUPPORT BINARY BYTES MSG");
			return;
		}
		// 4、handler biz msg
		final String originMsg = ((TextWebSocketFrame) frame).text();
		logger.info("RECEIVE ORIGIN_MSG:{}", originMsg);
		final IMsg msg = new Gson().fromJson(originMsg, IMsg.class);
		try {
			DebugSocketEcho.put(msg.getSid(),ctx.channel());
		} catch (Exception e) {
			logger.info("put channel failed:{}", msg.getSid());
		}
		
		if (msg.isHeartBeat())
			return;
		try {
			
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					try {
						IMsg targetMsg = (IMsg)Class.forName(MsgEnum.getByType(msg.getType()).getClazz()).newInstance();
						if(targetMsg instanceof BreakPointMsg){
							BreakPointMsg msg = new Gson().fromJson(originMsg, BreakPointMsg.class);
							debugService.execBreakpointSock(ctx.channel(), msg);
						}else if(targetMsg instanceof StepMsg){
							StepMsg msg = new Gson().fromJson(originMsg, StepMsg.class);
							debugService.execStepSock(ctx.channel(), msg);
						}else if(targetMsg instanceof FieldMsg){
							FieldMsg msg = new Gson().fromJson(originMsg, FieldMsg.class);
							debugService.execAccesswatchSock(ctx.channel(), msg);
						}else if(targetMsg instanceof DisConnectMsg){
							DisConnectMsg msg = new Gson().fromJson(originMsg, DisConnectMsg.class);
							debugService.execDisposeVm(ctx.channel(), msg);
						}else if(targetMsg instanceof StatMsg){
							StatMsg msg = new Gson().fromJson(originMsg, StatMsg.class);
							debugService.execStatVm(ctx.channel(), msg);
						}
					} catch (Exception e) {
						e.getMessage();
					}
					
					// shellService.execForSock(ctx.channel(), hostname, port,
					// user, "", CMDUtils.parseCmd(shell));

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			ctx.channel().writeAndFlush(new TextWebSocketFrame(e.getMessage()));
		}

	}

	private void handleHandShake(ChannelHandlerContext ctx,
			FullHttpRequest request) {
		// 1、web socket version check
		if (!request.decoderResult().isSuccess()
				|| (!"websocket".equals(request.headers().get("Upgrade")))) {
			sendHttpResponse(ctx, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		
		// 2、 login auth check
		String uri = request.uri();
		String sid = getSid(uri);

		// TODO
		if (!debugSocketAuthService.loginCheck(sid)) {
			logger.error("HIVE SOCKET LOGIN CHECK FAIL,uri:{}", uri);
			sendHttpResponse(ctx, new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED));
			return;
		}
		// 3、create handshaker
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
				DebugSocketServer.WEBSOCKET_URL, null, false);
		handshaker = wsFactory.newHandshaker(request);
		if (handshaker == null) {
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx
					.channel());
			logger.error("CREATE handshaker fail");
			return;
		}
		handshaker.handshake(ctx.channel(), request);

		// 5、add channel to group
		// HiveSocketChannelGroup.add(policyEnum, customerId, ctx.channel());
	}

	private static void sendHttpResponse(ChannelHandlerContext ctx,
			DefaultFullHttpResponse res) {
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
		if (null == map)
			return null;
		return map.get("sid");
	}

	private static Map<String, String> parseAttributes(String uri) {
		try {
			if (StringUtils.isBlank(uri))
				return null;
			String[] arr = uri.split("\\?")[1].split("&");
			Map<String, String> map = new HashMap<String, String>(arr.length);
			for (String kv : arr) {
				String[] s = kv.split("=");
				if (s.length != 2)
					continue;
				map.put(s[0], s[1]);
			}
			return map;
		} catch (Exception ex) {
		}
		return null;
	}
}