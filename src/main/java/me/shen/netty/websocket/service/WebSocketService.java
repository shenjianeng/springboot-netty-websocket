package me.shen.netty.websocket.service;

import io.netty.channel.Channel;

import java.util.List;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
public interface WebSocketService {

    /**
     * 订阅topic
     */
    void registerTopics(String channelId, List<String> topics);

    /**
     * 注册活跃channel
     */
    void addClient(Channel channel);

    /**
     * 往topic发送消息
     */
    void sendMessage(List<String> topics, String msgText);

    /**
     * 获取当前在线用户数
     */
    int clientNumber();
}
