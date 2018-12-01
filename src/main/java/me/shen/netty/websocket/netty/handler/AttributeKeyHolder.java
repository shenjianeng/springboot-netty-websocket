package me.shen.netty.websocket.netty.handler;

import io.netty.util.AttributeKey;

import java.util.List;

/**
 * @author shenjianeng
 * @date 2018/12/1
 */
public interface AttributeKeyHolder {

    AttributeKey<Long> USER_ID_ATTRIBUTE_KEY = AttributeKey.valueOf("userId");


    AttributeKey<List<String>> TOPIC_ATTRIBUTE_KEY = AttributeKey.valueOf("topics");


}
