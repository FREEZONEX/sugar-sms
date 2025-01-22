package org.niiish32x.sugarsms.app.websocket;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.app.event.AlertRecordChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocketServer
 *
 * @author shenghao ni
 * @date 2025.01.22 10:47
 */

@ServerEndpoint("/api/alert/websocket/{id}")
@Component
@Slf4j
public class AlertWebSocketServer  {

    private Session session;

    private static AlertService alertService;

    private static AlertRecordRepo alertRecordRepo;


    private String id;


    private static CopyOnWriteArraySet<AlertWebSocketServer> webSockets = new CopyOnWriteArraySet<>();

    /**
     * springboot 管理的是单例  和 websocket 多对象冲突
     * 因此采用方法注入的方式
     * @param alertService
     */
    @Autowired
    public void setAlertService(AlertService alertService) {
        AlertWebSocketServer.alertService = alertService;
    }

    @Autowired
    public void setAlertRecordRepo(AlertRecordRepo alertRecordRepo) {
        AlertWebSocketServer.alertRecordRepo = alertRecordRepo;
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSockets.add(this);
    }


    @EventListener(classes = AlertRecordChangeEvent.class)
    void onAlertRecordChangeEvent(AlertRecordChangeEvent event) {
        sendMessage("records change");
    }

    public void sendMessage(String msg) {
        webSockets.forEach(webSocketServer -> webSocketServer.session.getAsyncRemote().sendText(msg));
    }
}
