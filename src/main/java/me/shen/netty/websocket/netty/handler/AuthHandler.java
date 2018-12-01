package me.shen.netty.websocket.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 身份认证
 *
 * @author shenjianeng
 * @date 2018/12/1
 */
@ChannelHandler.Sharable
@Component
@Slf4j
public class AuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final String USER_COOKIE_NAME = "currentUser";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest httpRequest) throws Exception {
        log.info("AuthHandler..." + channelHandlerContext.channel().id());

        String cookie = httpRequest.headers().get(HttpHeaderNames.COOKIE);

        if (StringUtils.isEmpty(cookie)) {

            HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED);

            channelHandlerContext.writeAndFlush(response);

            return;
        }

        QueryStringDecoder query = new QueryStringDecoder(cookie, false);
        List<String> cookies = query.parameters().get(USER_COOKIE_NAME);
        //TODO 权限校验,获取userId

        long userId = ThreadLocalRandom.current().nextLong();

        channelHandlerContext.channel().attr(AttributeKeyHolder.USER_ID_ATTRIBUTE_KEY).set(userId);

        //增加引用次数,将数据传入下一个channel中
        channelHandlerContext.fireChannelRead(httpRequest.retain());
    }

}
