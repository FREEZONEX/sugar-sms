package org.niiish32x.sugarsms.app.job;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;

import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.queue.AlertMessageQueue;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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


    /**
     * fixedDelay 本次任务执行完后 10秒后 再执行下一次
     */
    @Scheduled(fixedDelay =  1000 * 10)
    void alertJob() {

        Result<List<AlertInfoDTO>> alertsResp  = alertService.getAlertsFromSupos();

        if(!alertsResp.isSuccess()) {
            log.error("alert api 获取异常");
            return;
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResp.getData();


        if(alertInfoDTOS.isEmpty()) {
            return;
        }

        for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {
            log.info("alert massage id {}",alertInfoDTO.getId());

            if (alertRecordRepo.findWithLimitByAlertId(alertInfoDTO.getId(),1) != null) {
//                log.info("alertId {} 已经发送过",alertInfoDTO.getId());
                continue;
            };
            alertMessageQueue.offer(alertInfoDTO);
        }

        if(!alertMessageQueue.isEmpty()) {
            alertService.publishAlertEvent();
        }

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
