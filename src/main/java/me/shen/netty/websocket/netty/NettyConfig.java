package me.shen.netty.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import me.shen.netty.websocket.netty.handler.AuthHandler;
import me.shen.netty.websocket.netty.handler.RegisterTopicHandler;
import me.shen.netty.websocket.netty.handler.RemoveInactivityChannelHandler;
import me.shen.netty.websocket.netty.handler.WebSocketHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
@Slf4j
@Configuration
public class NettyConfig implements ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    @Autowired
    private NettyProperties nettyProperties;

    @Autowired
    private WebSocketHandler webSocketHandler;

    @Autowired
    private RemoveInactivityChannelHandler removeInactivityChannelHandler;

    @Autowired
    private AuthHandler authHandler;

    @Autowired
    private RegisterTopicHandler registerTopicHandler;

    @Bean
    public EventLoopGroup boosGroup() {
        return new NioEventLoopGroup(nettyProperties.getBossThreads());
    }

    @Bean
    public EventLoopGroup workerGroup() {
        return new NioEventLoopGroup(nettyProperties.getWorkerThreads());
    }


    @Bean
    public ServerBootstrap serverBootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup(), workerGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                                .addLast(new IdleStateHandler(nettyProperties.getReaderIdle(),
                                        nettyProperties.getWorkerThreads()
                                        , nettyProperties.getAllIdle()))
                                .addLast(removeInactivityChannelHandler)
                                //http upgrade ,所以需要http相关配置
                                .addLast(new HttpServerCodec())
                                .addLast(new HttpObjectAggregator(1024 * 64))
                                .addLast(new ChunkedWriteHandler())
                                //身份认证
                                .addLast(authHandler)
                                //订阅topic
                                .addLast(registerTopicHandler)
                                .addLast(new WebSocketServerProtocolHandler(nettyProperties.getUrl()))
                                .addLast(webSocketHandler);

                    }
                })
                .option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, nettyProperties.isKeepAlive());

        return bootstrap;

    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            serverBootstrap().bind(nettyProperties.getPort()).sync();
            log.info("netty web socket started on port: " + nettyProperties.getPort());
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        boosGroup().shutdownGracefully();
        workerGroup().shutdownGracefully();
    }
}
