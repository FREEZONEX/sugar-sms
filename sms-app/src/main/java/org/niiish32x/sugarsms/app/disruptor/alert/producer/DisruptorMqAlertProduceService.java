package org.niiish32x.sugarsms.app.disruptor.alert.producer;

import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;

/**
 * DisruptorMqService
 *
 * @author shenghao ni
 * @date 2025.01.16 10:38
 */
public interface DisruptorMqAlertProduceService {
    boolean produce(AlertEvent alertEvent);

    boolean produceAlertRecordEvent(AlertRecordEvent alertRecordEvent);

}
