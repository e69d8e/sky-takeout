package com.li.common.utils;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Slf4j
@Component
@ServerEndpoint("/ws/{sid}")
public class WebSocketUtil {
    // 存放会话对象
    private static final Map<String, Session> sessionPool = new HashMap<>();
    // 建立连接成功后触发
    @OnOpen
    public void onOpen(Session session) {
        // 获取用户id
        String sid = session.getPathParameters().get("sid");
        // 将用户id作为key，session作为value，保存到map中
        sessionPool.put(sid, session);
    }
    // 收到客户端消息时触发
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到来自" + sid + "的消息:" + message);
    }
    // 关闭连接时触发
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接关闭");
        sessionPool.remove(sid);
    }
    // 发生错误时触发
    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }

    public void sendAllClient(String message) {
        // 遍历所有会话对象，发送消息
        Collection<Session> sessions = sessionPool.values();
        sessions.forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
