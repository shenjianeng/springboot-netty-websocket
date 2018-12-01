package me.shen.netty.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
@Configuration
public class RedisConfig {

    public static final String REDIS_TOPIC = "chat";

    @Autowired
    private WebSocketServiceImpl.RedisMessageReceiver redisMessageReceiver;

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(REDIS_TOPIC));

        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter() {
        MessageListenerAdapter adapter = new MessageListenerAdapter();
        adapter.setDefaultListenerMethod(WebSocketServiceImpl.RedisMessageReceiver.LISTENER_METHOD);
        adapter.setDelegate(redisMessageReceiver);
        adapter.setSerializer(new Jackson2JsonRedisSerializer<>(RedisMessage.class));
        adapter.afterPropertiesSet();
        return adapter;
    }

    @Bean
    public RedisTemplate<String, RedisMessage> redisMessageRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, RedisMessage> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(RedisMessage.class));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
