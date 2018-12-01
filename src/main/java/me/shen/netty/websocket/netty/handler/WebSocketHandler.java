package me.shen.netty.websocket.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import me.shen.netty.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * websocket 消息处理
 *
 * @author shenjianeng
 * @date 2018/12/1
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        log.info("WebSocketHandler..." + ctx.channel().id());

        final Long userId = ctx.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).get();
        final List<String> topics = ctx.channel().attr(AttributeKeyHolder.TOPIC_ATTRIBUTE_KEY).get();

        final String msgText = msg.text();

        log.info("userId: {} ,topics: {},send: {}", userId, topics, msgText);

        webSocketService.sendMessage(topics, userId + "发送: " + msgText);

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        webSocketService.addClient(ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

}

