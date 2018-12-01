package me.shen.netty.websocket.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import me.shen.netty.websocket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 定期将不活跃的channel 关闭
 *
 * @author shenjianeng
 * @date 2018/12/1
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RemoveInactivityChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event == IdleStateEvent.ALL_IDLE_STATE_EVENT) {
                log.info("移除不活跃channel: {},当前在线用户: {}", ctx.channel().id(), webSocketService.clientNumber());
                ctx.channel().close();
            }
        }
    }
}
