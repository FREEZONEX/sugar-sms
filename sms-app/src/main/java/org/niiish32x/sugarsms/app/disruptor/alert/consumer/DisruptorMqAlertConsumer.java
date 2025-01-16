package org.niiish32x.sugarsms.app.disruptor.alert.consumer;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.niiish32x.sugarsms.message.app.SendMessageService;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * DisruptorMqConsumer
 *
 * @author shenghao ni
 * @date 2025.01.16 10:49
 */

@Slf4j
@Component
public class DisruptorMqAlertConsumer implements EventHandler<AlertEvent> {
    private final String SUGAR_ALERT_EMAIL_SUBJECT = "sugar-plant-alert";

    @Autowired
    SendMessageService sendMessageService;

    @Autowired
    AlertRecordRepo alertRecordRepo;

    @Override
    public void onEvent(AlertEvent alertEvent, long l, boolean b) throws Exception {
        Long id = alertEvent.getAlertRecordId();
        AlertRecordEO alertRecordEO = alertRecordRepo.findById(id);

        String receiver = alertRecordEO.getType() == MessageType.SMS ? alertRecordEO.getPhone() : alertRecordEO.getEmail();
        String message = alertRecordEO.getContent();
        MessageType messageType = alertRecordEO.getType();

        try {
            Retrys.doWithRetry(()-> alert(id,messageType,message,receiver)  , r -> r ,3 ,1000) ;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    boolean alert(Long id ,MessageType messageType,String message,String receiver) {

        boolean res = false;

        if (messageType == MessageType.SMS) {
            Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(receiver, message);
            res = smsResp.isSuccess();
        } else if (messageType == MessageType.EMAIL) {
            res = sendMessageService.sendEmail(receiver, SUGAR_ALERT_EMAIL_SUBJECT, message);
        }

        if (res) {
            boolean updateRes = alertRecordRepo.updateStatusById(id,true);
            if (!updateRes) {
                log.error("alert: id:{} {} 更新状态失败", id , receiver);
                return false;
            }
        }else {
            log.error("alert: id:{} {} 发送失败", id, receiver);
            return false;
        }

        return true;
    }

}
