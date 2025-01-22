package org.niiish32x.sugarsms.app.disruptor.alert.consumer;

import com.lmax.disruptor.EventHandler;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.alert.app.command.ProduceAlertRecordCommand;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRepo;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;
import org.niiish32x.sugarsms.app.event.AlertRecordChangeEvent;
import org.niiish32x.sugarsms.common.event.EventBus;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * DisruptorMqAlertRecordConsumer
 *
 * @author shenghao ni
 * @date 2025.01.16 14:18
 */

@Component
public class DisruptorMqAlertRecordConsumer implements EventHandler<AlertRecordEvent> {

    @Resource
    AlertService alertService;

    @Autowired
    AlertRepo alertRepo;

    @Override
    public void onEvent(AlertRecordEvent event, long sequence, boolean endOfBatch) throws Exception {
        AlertEO alertEO = event.getAlertEO();

        try {
            Retrys.doWithRetry(() ->
                    alertService.productAlertRecord(alertEO), result -> result , 3, 3 * 1000);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

        alertEO.setFinishGenerateAlertRecord(true);
        alertRepo.saveOrUpdate(alertEO);
        EventBus.publishEvent(new AlertRecordChangeEvent(this));
    }
}
