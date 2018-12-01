package me.shen.netty.websocket.netty;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "me.shen.netty")
public class NettyProperties implements Serializable {

    private static final long serialVersionUID = 5210914393003529116L;

    /**
     * WS连接端口
     */
    private int port;

    /**
     * Boos EventLoopGroup 线程数量
     */
    private int bossThreads;

    /**
     * Worker EventLoopGroup 线程数量
     */
    private int workerThreads;

    /**
     * keepAlive 参数
     */
    private boolean keepAlive;

    /**
     * backlog 参数
     */
    private int backlog;

    /**
     * WS连接地址
     */
    private String url;

    /**
     * 读空闲时长,单位秒
     */
    private int readerIdle;

    /**
     * 写空闲时长,单位秒
     */
    private int writerIdle;

    /**
     * 读 + 写 空闲时长,单位秒
     */
    private int allIdle;

}
