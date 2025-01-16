package org.niiish32x.sugarsms.app.disruptor.alert.producer;

import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;

import java.util.concurrent.CountDownLatch;

/**
 * DisruptorMqService
 *
 * @author shenghao ni
 * @date 2025.01.16 10:38
 */
public interface DisruptorMqAlertProduceService {
    void produceAlertEvent(AlertEvent alertEvent);

    boolean produceAlertRecordEvent(AlertRecordEvent alertRecordEvent);

}
