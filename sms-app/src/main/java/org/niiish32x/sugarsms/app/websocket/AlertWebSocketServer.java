package org.niiish32x.sugarsms.app.websocket;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.api.alert.dto.AlertRecordDTO;
import org.niiish32x.sugarsms.app.event.AlertRecordChangeEvent;
import org.niiish32x.sugarsms.app.event.AlertRecordsGenerateEvent;
import org.niiish32x.sugarsms.common.result.PageResult;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.*;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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
