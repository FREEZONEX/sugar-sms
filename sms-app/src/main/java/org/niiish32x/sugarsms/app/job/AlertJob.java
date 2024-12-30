package org.niiish32x.sugarsms.app.job;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alarm.app.command.SavaAlarmCommand;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
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

    /**
     * fixedDelay 本次任务执行完后 10秒后 再执行下一次
     */
    @Scheduled(fixedDelay = 1000 * 10)
    void alertJob() {
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

            // 批量查询已发送过的 alertId
            List<Long> existingAlertIds = alertRecordRepo.findExistingAlertIds(alertInfoDTOS.stream()
                    .map(AlertInfoDTO::getId)
                    .collect(Collectors.toList()));

            for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {
                if (existingAlertIds.contains(alertInfoDTO.getId())) {
                    log.info("alertId {} 已经发送过", alertInfoDTO.getId());
                    continue;
                }

                Result<List<AlarmDTO>> alarmsFromSupos = alarmService.getAlarmsFromSupos(
                        AlarmRequest.builder()
                                .attributeEnName(alertInfoDTO.getSourcePropertyName())
                                .build());

                if (!alarmsFromSupos.isSuccess() || alarmsFromSupos.getData() == null || alarmsFromSupos.getData().isEmpty()) {
                    log.error("获取alarmsFromSupos 报警详情信息异常或为空: {}", alarmsFromSupos.getMessage());
                    continue;
                }

                AlarmDTO alarmDTO = alarmsFromSupos.getData().get(0);

                SavaAlarmCommand savaAlarmCommand = new SavaAlarmCommand(alarmDTO);

                Result<Boolean> saveResult = alarmService.save(savaAlarmCommand);
                if (!saveResult.isSuccess()) {
                    log.error("保存报警信息失败: {}", saveResult.getMessage());
                    continue;
                }

                alertMessageQueue.offer(alertInfoDTO);
            }

            if (!alertMessageQueue.isEmpty()) {
                alertService.publishAlertEvent();
            }
        } catch (Exception e) {
            log.error("定时任务执行过程中发生异常", e);
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
