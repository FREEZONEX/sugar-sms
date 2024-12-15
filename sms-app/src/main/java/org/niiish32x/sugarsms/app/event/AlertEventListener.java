package org.niiish32x.sugarsms.app.event;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * AlertEventListener
 *
 * @author shenghao ni
 * @date 2024.12.15 14:18
 */

@Component
@Slf4j
public class AlertEventListener {

    @Autowired
    AlertService alertService;


    @EventListener
    public void receiveEvent(AlertEvent alertEvent) {
        log.info("接收到 报警事件");
        alertService.consumeAlertEvent();
    }
}
