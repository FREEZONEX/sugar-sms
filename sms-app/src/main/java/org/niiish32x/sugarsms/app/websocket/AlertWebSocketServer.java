package org.niiish32x.sugarsms.app.websocket;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.api.alert.dto.AlertRecordDTO;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.List;

/**
 * WebSocketServer
 *
 * @author shenghao ni
 * @date 2025.01.22 10:47
 */

@ServerEndpoint("/api/alert/websocket/{id}")
@Component
@Slf4j
public class AlertWebSocketServer {

    private static AlertService alertService;

    private String id;

    /**
     * springboot 管理的是单例  和 websocket 多对象冲突
     * 因此采用方法注入的方式
     * @param alertService
     */
    @Autowired
    public void setAlertService(AlertService alertService) {
        AlertWebSocketServer.alertService = alertService;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        System.out.println("连接建立");
    }

    @OnClose
    public void onClose() {

    }

    /**
     * 接收消息
     */
    @OnMessage
    public void onMessage(String message,Session session)  {
        Result<List<AlertRecordDTO>> listResult = alertService.queryAlertRecords();

        try {
            System.out.println(message);
            session.getBasicRemote().sendText(JSON.toJSONString(listResult));
        }catch (Exception e) {
            System.out.println(e);
        }

    }

}
