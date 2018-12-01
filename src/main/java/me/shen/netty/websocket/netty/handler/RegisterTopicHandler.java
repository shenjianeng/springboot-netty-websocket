package me.shen.netty.websocket.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import lombok.extern.slf4j.Slf4j;
import me.shen.netty.websocket.netty.NettyProperties;
import me.shen.netty.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 订阅topic
 *
 * @author shenjianeng
 * @date 2018/12/1
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class RegisterTopicHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String TOPIC_KEY = "topic";

    @Autowired
    private NettyProperties nettyProperties;

    @Autowired
    private WebSocketService webSocketService;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        log.info("RegisterTopicHandler..." + channelHandlerContext.channel().id());

        final Long userId = channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).get();
        Assert.notNull(userId, "userId can not be null");

        String uri = fullHttpRequest.uri();

        QueryStringDecoder query = new QueryStringDecoder(uri, true);

        final List<String> topics = query.parameters().get(TOPIC_KEY);

        log.info("user: {} ,topic: {}", userId, topics.toString());

        webSocketService.registerTopics(channelHandlerContext.channel().id().asLongText(), topics);

        //传递给WebSocketHandler
        channelHandlerContext.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).set(topics);

        //重置URL,否则进入WebSocketServerProtocolHandler,会连接失败
        fullHttpRequest.setUri(nettyProperties.getUrl());


        //增加引用次数,将数据传入下一个channel中
        channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
    }
}
