package me.shen.netty.websocket.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisMessage implements Serializable {
    private String topic;

    private String message;
}
