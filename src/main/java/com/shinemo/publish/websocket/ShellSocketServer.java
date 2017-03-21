package com.shinemo.publish.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shinemo.publish.service.ShellService;
import com.shinemo.publish.utils.ServerConfig;

public class ShellSocketServer {
	
	private static final Logger logger = LoggerFactory.getLogger(
			ShellSocketServer.class);
	
    private final static int PORT = Integer.parseInt(ServerConfig.get("websocke.port","11362"));

    public final static String WEBSOCKET_URL = "ws://localhost:" + PORT + "/shell";
    @Resource
    private ShellSocketAuthService shellSocketAuthService;
    @Resource
    private ShellService shellService;

    public ShellSocketServer() {
    }

    public ShellSocketServer(ShellSocketAuthService shellSocketAuthService,
    		ShellService shellService) {
        this.shellSocketAuthService = shellSocketAuthService;
        this.shellService = shellService;
    }

    public void run() {
    	logger.error("init websocket server ..");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel e) throws Exception {
                    e.pipeline().addLast("http-codec", new HttpServerCodec());
                    e.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                    e.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                    e.pipeline().addLast("handler", new ShellSocketHandler(shellSocketAuthService, shellService));
                }
            });
            Channel ch = b.bind(PORT).sync().channel();
            ch.closeFuture().sync();
        	logger.error("websocket server start success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String... args) {
        new ShellSocketServer(null, null).run();
    }
}