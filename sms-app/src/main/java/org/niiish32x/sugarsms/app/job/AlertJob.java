package org.niiish32x.sugarsms.app.job;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alarm.app.command.SavaAlarmCommand;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.alert.app.command.ProductAlertRecordCommand;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;

import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.queue.AlertMessageQueue;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * AlertJob
 *
 * @author shenghao ni
 * @date 2024.12.13 11:03
 */

@Slf4j
@Component
public class AlertJob {

    @Autowired
    AlertService alertService;

    @Autowired
    AlertMessageQueue alertMessageQueue;

    @Autowired
    AlertRecordRepo alertRecordRepo;


    @Autowired
    AlarmService alarmService;

    @Scheduled(fixedDelay = 1000 * 10)
    void getAlert() {
        try {
            Result<List<AlertInfoDTO>> alertsResp = alertService.getAlertsFromSupos();

            if (!alertsResp.isSuccess()) {
                log.error("alert api 获取异常: {}", alertsResp.getMessage());
                return;
            }

            List<AlertInfoDTO> alertInfoDTOS = alertsResp.getData();
            if (alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
                return;
            }


            for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {
                CompletableFuture.supplyAsync(()-> alertService.productAlertRecord(new ProductAlertRecordCommand(alertInfoDTO))) ;
            }

            CompletableFuture.allOf();
        } catch (Exception e) {
            log.error("定时任务执行过程中发生异常", e);
        }
    }

    @Scheduled(fixedDelay = 1000 * 5)
    void alert() {
        alertService.consumeAlertEvent();
    }

    /**
     * 每周六 凌晨 3点
     */
    @Scheduled(cron = "0 0 3 ? * SAT")
    void cleanJob() {
        // 清理过去两周的消息
        alertService.cleanAlertPastDays(14);
    }
}
