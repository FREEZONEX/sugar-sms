package org.niiish32x.sugarsms.app.job;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alert.app.command.ProduceAlertRecordCommand;
import org.niiish32x.sugarsms.alert.app.command.SaveAlertCommand;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;

import org.niiish32x.sugarsms.alert.domain.repo.AlertRepo;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.producer.DisruptorMqAlertProduceService;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.manager.thread.GlobalThreadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;

/**
 * AlertJob
 *
 * @author shenghao ni
 * @date 2024.12.13 11:03
 */

@Slf4j
@Component
public class AlertJob {

    private static final Cache<String,Integer> ALERT_SEND_MAP = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .initialCapacity(1000)
            .build();

    private final String EMAIL_ALERT_KEY = "ALERT_EMAIL_%s";
    private final String SMS_ALERT_KEY = "ALERT_SMS_%s";

    @Autowired
    AlertService alertService;


    @Autowired
    AlertRecordRepo alertRecordRepo;


    @Autowired
    AlarmService alarmService;

    @Autowired
    ApplicationEventPublisher publisher;

    @Autowired
    DisruptorMqAlertProduceService disruptorMqAlertProduceService;


    @Autowired
    AlertRepo alertRepo;

    @Scheduled(fixedDelay = 1000 * 10)
    void getAlert(){
        Result<List<AlertInfoDTO>> alertsResp = alertService.getAlertsFromSupos();

        if (!alertsResp.isSuccess()) {
            log.error("alert api 获取异常: {}", alertsResp.getMessage());
            return;
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResp.getData();

        for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {
            if (alertRepo.findByAlertId(alertInfoDTO.getId()) != null) {
                continue;
            }
            alertService.saveAlert(new SaveAlertCommand(alertInfoDTO));
        }
    }

    @Scheduled(fixedDelay = 1000 * 5)
    void generateRecord() {
        try {

            List<AlertEO> unFinishedAlerts = alertRepo.findUnFinishedAlerts(5);

            for (AlertEO alertEO: unFinishedAlerts) {
                alertService.productAlertRecord(alertEO);
//                disruptorMqAlertProduceService.produceAlertRecordEvent(
//                        AlertRecordEvent.builder()
//                                .alertEO(alertEO)
//                                .build()
//                );

                alertEO.setFinishGenerateAlertRecord(true);
                alertRepo.saveOrUpdate(alertEO);
            }
        } catch (Exception e) {
            log.error("定时任务执行过程中发生异常", e);
        }
    }

//    @Scheduled(fixedDelay = 2 * 1000)
    void alertSms () throws InterruptedException {

        log.info(">>>>>>>>>>> start Sms alert >>>>>>>>>>>>>>>>>");

        /**
         * 先查Ids 再一条条根据去查 避免过多数据加载到内存
         */
        List<Long> alertRecordIds = alertRecordRepo.findPendingSendSmsAlertIds(100);
        RateLimiter limiter =  RateLimiter.create(30);

        if (alertRecordIds == null || alertRecordIds.isEmpty()) {
            return;
        }

        for (Long id : alertRecordIds) {
            String key = String.format(SMS_ALERT_KEY, id);

            if (ALERT_SEND_MAP.getIfPresent(key) != null ) {
                continue;
            }

            limiter.tryAcquire();
            disruptorMqAlertProduceService.produceAlertEvent(
                    AlertEvent.builder()
                            .alertRecordId(id)
                            .build()
            );

            ALERT_SEND_MAP.put(key,1);
        }

        log.info(">>>>>>>>>>> finish Sms alert >>>>>>>>>>>>>>>>>");
    }

    /**
     * email 邮件支持 最大连接数 远远小于 sms 要严格限制 流量
     */
    @Scheduled(fixedDelay = 2 * 1000)
    void alertEmail () throws InterruptedException {



        List<Long> alertRecordIds = alertRecordRepo.findPendingSendEmailAlertIds(10);

        if (alertRecordIds == null || alertRecordIds.isEmpty()) {
            return;
        }

        log.info(">>>>>>>>>>> start email alert >>>>>>>>>>>>>>>>>");

        RateLimiter limiter =  RateLimiter.create(2);

        for (Long id : alertRecordIds) {
            String key = String.format(EMAIL_ALERT_KEY, id);

            if (ALERT_SEND_MAP.getIfPresent(key) != null ) {
                continue;
            }

            limiter.tryAcquire(1);
            disruptorMqAlertProduceService.produceAlertEvent(
                    AlertEvent.builder()
                            .alertRecordId(id)
                            .build()
            );

            ALERT_SEND_MAP.put(key,1);
        }

        log.info(">>>>>>>>>>> finish email alert >>>>>>>>>>>>>>>>>");
    }


    /**
     * 每周六 凌晨 3点
     */
//    @Scheduled(cron = "0 0 3 ? * SAT")
    void cleanJob() {
        // 清理过去两周的消息
        alertService.cleanAlertPastDays(14);
    }
}
