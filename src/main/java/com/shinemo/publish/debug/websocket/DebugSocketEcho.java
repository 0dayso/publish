package com.shinemo.publish.debug.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.shinemo.publish.api.util.SpringUtil;
import com.shinemo.publish.common.ResultVO;
import com.shinemo.publish.debug.websocket.msg.OutStepMsg;
import com.shinemo.publish.redis.service.RedisService;
import com.shinemo.publish.redis.util.RedisKey;

public class DebugSocketEcho {
	
	
	private static Logger logger = LoggerFactory.getLogger(DebugSocketEcho.class);
	
	
	private static ConcurrentHashMap<Long,Channel> channels = new ConcurrentHashMap<Long,Channel>();
	
	
	public final static int OK_CODE = 200;
	public final static int STEPOVER_CODE = 201;
	public final static int RELEASE_CODE = 202;
	public final static int PRINT_CODE = 300;
	
	
	/**
	 * 流数据
	 * @param in
	 * @param channel
	 */
	public static void processStdStream(InputStream in,String sid) {
		final Channel channel = get(sid);
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				final String linetmp = line;
				channel.eventLoop().execute(new Runnable() {
					@Override
					public void run() {
						channel.writeAndFlush(new TextWebSocketFrame(linetmp));
					}
				});
			}
		} catch (Exception e) {
			logger.error("processStderr err ：", e);
		} finally {
			try {
				in.close();
			} catch (Exception ex) {
			}
		}
	}
	
	/**
	 * 字符数据 用于打印
	 * @param in
	 * @param channel
	 * @param sb
	 */
	public static void processText(final String text,String sid) {
		ResultVO result = ResultVO.success(text, PRINT_CODE);
		processResult(result, sid);
	}
	
	
	/**
	 * 字符数据
	 * @param in
	 * @param channel
	 * @param sb
	 */
	public static void processResult(final ResultVO msg,String sid) {
		final Channel channel = get(sid);
		try {
			channel.eventLoop().execute(new Runnable() {
				@Override
				public void run() {
					Gson gson = new Gson();
					String json = gson.toJson(msg);
					channel.writeAndFlush(new TextWebSocketFrame(json));
				}
			});
		} catch (Exception e) {
			logger.error("processText err ：", e);
		} finally {
			
		}
	}
	
	
	public static void processText(final String text,long uid) {
		final Channel channel = channels.get(uid);
		try {
			channel.eventLoop().execute(new Runnable() {
				@Override
				public void run() {
					channel.writeAndFlush(new TextWebSocketFrame(text));
				}
			});
		} catch (Exception e) {
			logger.error("processText err ：", e);
		} finally {
			
		}
	}
	
	
	
	public static Channel get(String sid){
		long uid = getUid(sid);
		return channels.get(uid);
	}
	
	public static void put(String sid,Channel channel){
		long uid = getUid(sid);
		channels.put(uid,channel);
	}
	
	
	private static long getUid(String sid){
		RedisService redisService = (RedisService)SpringUtil.getBean("redisService");
		String namespace = RedisKey.getSessionKey(sid);
	    String uid = redisService.hget(6, namespace, "userId", String.class);
	    if(uid==null){
	    	throw new RuntimeException("uid is null" + sid); 
	    }
	    return Long.parseLong(uid);
	}
	
	

}
