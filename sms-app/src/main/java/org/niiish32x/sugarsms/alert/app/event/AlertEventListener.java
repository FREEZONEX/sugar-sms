package org.niiish32x.sugarsms.alert.app.event;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.niiish32x.sugarsms.manager.thread.GlobalThreadManager;
import org.niiish32x.sugarsms.message.app.SendMessageService;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

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

    static int maximumPoolSize = 300;
    static int coolPoolSize = 100;

    static ConcurrentHashMap <String,Boolean> map = new ConcurrentHashMap<>();

    static RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

    private static final ThreadPoolExecutor poolExecutor = GlobalThreadManager.getInstance().allocPool(coolPoolSize, maximumPoolSize,
            10 * 60 * 1000, 3000, "sugar-sms-alert-pool", true ,handler);

    @EventListener
    public void receiveEvent(AlertEvent alertEvent) {
        log.info("开始发送报警事件");

        for (Long id : alertEvent.getAlertIds()) {
            AlertRecordEO record = alertRecordRepo.findById(id);

            if (record.getStatus() || map.containsKey(id + record.getType().name())) {
                continue;
            }

            map.putIfAbsent(id + record.getType().name(),true);

            CompletableFuture.runAsync(()-> {
                try {
                    Retrys.doWithRetry(()-> alert(record)  , r -> r ,3 ,1000) ;
                } catch (Throwable e) {
                    throw new RuntimeException("报警通知 发送异常",e);
                }
            },poolExecutor);
        }
    }

    boolean alert(AlertRecordEO record) {
        if (record.getType() == MessageType.SMS) {
            Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(record.getPhone(), record.getContent());
            if (smsResp.isSuccess()) {
                log.info("alert: {} {} 发送成功", record.getAlertId(), record.getPhone());
                record.setStatus(true);
                boolean updateRes = alertRecordRepo.update(record);
                if (!updateRes) {
                    log.error("alert: {} {} 更新状态失败", record.getAlertId(), record.getPhone());
                    return false;
                }
            }else {
                log.error("alert: {} {} 发送失败", record.getAlertId(), record.getPhone());
                map.remove(record.getAlertId() + record.getType().name());
                return false;
            }
        } else if (record.getType() == MessageType.EMAIL) {
            boolean res = sendMessageService.sendEmail(record.getEmail(), SUGAR_ALERT_EMAIL_SUBJECT, record.getContent());
            if (res) {
                log.info("alert: {} {} 发送成功", record.getAlertId(), record.getEmail());
                record.setStatus(true);
                boolean updateRes = alertRecordRepo.update(record);
                if (!updateRes) {
                    log.error("alert: {} {} 更新状态失败", record.getAlertId(), record.getEmail());
                    return false;
                }
            }else {
                log.error("alert: {} {} 发送失败", record.getAlertId(), record.getEmail());
                map.remove(record.getAlertId() + record.getType().name());
                return false;
            }
        }

        map.remove(record.getAlertId() + record.getType().name());
        return true;
    }
}
