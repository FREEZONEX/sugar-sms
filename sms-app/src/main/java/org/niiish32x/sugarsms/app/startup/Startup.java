package org.niiish32x.sugarsms.app.startup;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Starup
 *
 * @author shenghao ni
 * @date 2025.01.21 16:41
 */

@Slf4j
@Component
public class Startup implements InitializingBean {

    @Autowired
    AlertService alertService;

    @Override
    public void afterPropertiesSet() throws Exception {
//        log.info("start ack all old alerts");
//        // 确认所有消息 防止重启后一次性发送过多
//        alertService.ackAlerts();
//        log.info("finish ack all old alerts");
    }
}
