package org.niiish32x.sugarsms.app.job;

import org.checkerframework.checker.units.qual.C;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * AlertJob
 *
 * @author shenghao ni
 * @date 2024.12.13 11:03
 */

@Component
public class AlertJob {

    @Autowired
    AlertService alertService;

//    @Scheduled(cron = "0/10 * * * * ?")
    void alert() {
        alertService.notifySugarUserByEmail();
        alertService.notifySugarUserBySms();
    }
}
