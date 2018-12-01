package me.shen.netty.websocket.service;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {

    private static final String REGISTER_MARK = "1";

    /**
     * 用于记录所有的在线客户端,netty会自动移除无效的channel
     */
    private static final ChannelGroup CLIENT_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, RedisMessage> redisMessageRedisTemplate;

    @Override
    public void registerTopics(String channelId, List<String> topics) {
        //<topic,channelId,mark>
        HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
        for (String topic : topics) {
            if (StringUtils.hasText(topic)) {
                opsForHash.put(topic, channelId, REGISTER_MARK);
                stringRedisTemplate.expire(topic, 2, TimeUnit.DAYS);
            }
        }
    }

    @Override
    public void addClient(Channel channel) {
        CLIENT_GROUP.add(channel);
    }

    @Override
    public void sendMessage(List<String> topics, String msgText) {
        topics.forEach(topic ->
                redisMessageRedisTemplate.convertAndSend(RedisConfig.REDIS_TOPIC, new RedisMessage(topic, msgText)));
    }

    @Override
    public int clientNumber() {
        return CLIENT_GROUP.size();
    }

    @Component
    public static class RedisMessageReceiver {

        @Autowired
        private StringRedisTemplate stringRedisTemplate;

        public static final String LISTENER_METHOD = "receiveMessage";

        public void receiveMessage(RedisMessage redisMessage) {
            final String topic = redisMessage.getTopic();
            final String message = redisMessage.getMessage();
            HashOperations<String, String, String> opsForHash = stringRedisTemplate.opsForHash();
            Map<String, String> channelMap = opsForHash.entries(topic);
            for (Channel channel : CLIENT_GROUP) {
                if (REGISTER_MARK.equals(channelMap.get(channel.id().asLongText()))) {
                    channel.writeAndFlush(new TextWebSocketFrame(message));
                }
            }
        }
    }

}
