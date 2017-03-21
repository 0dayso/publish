package com.shinemo.publish.debug.websocket;

import io.netty.channel.Channel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DebugSocketChannelGroup {
    private static final Logger logger = LoggerFactory.getLogger(
            DebugSocketChannelGroup.class);
    private static Map<String, Channel> privateGroupMap = new HashMap<String, Channel>();
    private static List<Channel> publicGroupList = new ArrayList<Channel>();

    public static void addPrivateChannel(String customerId, Channel ch) {
        logger.info("WEB_SOCKET_ADD_PRIVATE_CHANNEL,customerId:{},channelId:{}",
                customerId, ch.id().asLongText());
        removePrivateChannel(customerId);  //if conflict,then close old channel
        privateGroupMap.put(customerId, ch);
    }

    public static boolean removePrivateChannel(String customerId) {
        if (StringUtils.isBlank(customerId)) return true;
        Channel channel = privateGroupMap.get(customerId);
        if (null == channel) return true;
        logger.info("WEB_SOCKET_REMOVE_PRIVATE_CHANNEL,customerId:{},channelId:{}",
                customerId, channel.id().asLongText());
        channel.disconnect();
        privateGroupMap.put(customerId, null);
        return true;
    }

    public static boolean removePrivateChannel(Channel channel) {
        String customerId = getCustomerId(channel);
        if (StringUtils.isBlank(customerId)) return true;
        logger.info("WEB_SOCKET_REMOVE_CHANNEL,customerId:{},channelId:{}",
                customerId, channel.id().asLongText());
        privateGroupMap.put(customerId, null);
        return true;
    }

    public static String getCustomerId(Channel ch) {
        if (null == ch) return null;
        if (null == privateGroupMap || privateGroupMap.size() == 0) return null;
        Iterator<Map.Entry<String, Channel>> iterator = privateGroupMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Channel> entry = iterator.next();
            if (null == entry.getValue()) continue;
            if (ch.id().asLongText().equals(entry.getValue().id().asLongText()))
                return entry.getKey();
        }
        return null;
    }

    public static Channel getPrivateChannel(String customerId) {
        return privateGroupMap.get(customerId);
    }

    public static Collection<Channel> privateChannels() {
        return privateGroupMap.values();
    }

    public static Set<String> keys() {
        return privateGroupMap.keySet();
    }

    public static void addPublicChannel(Channel ch) {
        logger.info("WEB_SOCKET_ADD_PUBLIC_CHANNEL,channelId:{}", ch.id().asLongText());
        publicGroupList.add(ch);
    }

    public static boolean removePublicChannel(Channel ch) {
        if (publicGroupList.size() == 0) return true;
        Iterator<Channel> iterator = publicGroupList.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            if (channel.equals(ch)) {
                iterator.remove();
                return true;
            }
        }
        return true;
    }

    public static List<Channel> publicChannels() {
        return publicGroupList;
    }

    public static void removeChannel(Channel ch) {
        removePublicChannel(ch);
        removePrivateChannel(ch);
    }
}
