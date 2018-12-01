使用Spring Boot 和 Netty 实现的 web socket,支持多节点部署,支持按topic收发消息


- `me.shen.netty.websocket.netty.handler.AuthHandler` : 用户信息认证

- `me.shen.netty.websocket.netty.handler.RegisterTopicHandler` : 获取当前用户所订阅的topic

- `me.shen.netty.websocket.netty.handler.RemoveInactivityChannelHandler` : 心跳检查,将长时间不活跃的用户清除

- `me.shen.netty.websocket.netty.handler.WebSocketHandler` : web socket 内容的收发