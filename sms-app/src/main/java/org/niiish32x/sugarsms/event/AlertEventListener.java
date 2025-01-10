package org.niiish32x.sugarsms.event;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.event.AlertEvent;
import org.niiish32x.sugarsms.message.app.SendMessageService;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * AlertEventListener
 *
 * @author shenghao ni
 * @date 2024.12.15 14:18
 */

@Component
@Slf4j
public class AlertEventListener {

    private final String SUGAR_ALERT_EMAIL_SUBJECT = "sugar-plant-alert";

    @Autowired
    AlertService alertService;


    @Autowired
    AlertRecordRepo alertRecordRepo;

    @Autowired
    SendMessageService sendMessageService;

    @EventListener
    public void receiveEvent(AlertEvent alertEvent) {
        log.info("接收到 报警事件");
        List<AlertRecordEO> failRecords = alertRecordRepo.findFailRecords();

        for (AlertRecordEO record : failRecords) {
            if (record.getType() == MessageType.SMS) {
                Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(record.getPhone(), record.getContent());
                if (smsResp.isSuccess()) {
                    log.info("alert: {} {} 发送成功", record.getAlertId(), record.getPhone());
                    record.setStatus(true);
                    alertRecordRepo.update(record);
                }else {
                    log.error("alert: {} {} 发送失败", record.getAlertId(), record.getPhone());
                    alertRecordRepo.update(record);
                }
            } else if (record.getType() == MessageType.EMAIL) {
                boolean res = sendMessageService.sendEmail(record.getEmail(), SUGAR_ALERT_EMAIL_SUBJECT, record.getContent());
                if (res) {
                    log.info("alert: {} {} 发送成功", record.getAlertId(), record.getEmail());
                    record.setStatus(true);
                    alertRecordRepo.update(record);
                }else {
                    log.error("alert: {} {} 发送失败", record.getAlertId(), record.getEmail());
                    alertRecordRepo.update(record);
                }
            }
        }
    }
}
