package org.niiish32x.sugarsms.app.disruptor.alert.producer.impl;

import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.producer.DisruptorMqAlertProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * DisruptorMqServiceImpl
 *
 * @author shenghao ni
 * @date 2025.01.16 10:38
 */

@Slf4j
@Component
public class DisruptorMqAlertProduceServiceImpl implements DisruptorMqAlertProduceService {

    @Autowired
    private RingBuffer<AlertEvent> alertEventRingBuffer;


    @Autowired
    private RingBuffer<AlertRecordEvent> alertRecordEventRingBuffer;


    @Override
    public void produceAlertEvent(AlertEvent alertEvent) {
        long next = alertEventRingBuffer.next();

        try {
            AlertEvent message = alertEventRingBuffer.get(next);
            message.setAlertRecordId(alertEvent.getAlertRecordId());
            alertEventRingBuffer.publish(next);
        }catch (Exception e){
            log.error("produce AlertEvent error: " + e.getMessage());
        }
    }

    @Override
    public boolean produceAlertRecordEvent(AlertRecordEvent alertRecordEvent) {
        long next = alertRecordEventRingBuffer.next();

        try {
            AlertRecordEvent event = alertRecordEventRingBuffer.get(next);
            event.setAlertInfoDTO(alertRecordEvent.getAlertInfoDTO());
        }catch (Exception e){
            log.error("produce AlertRecordEvent error: " + e.getMessage());
            return false;
        }

        alertRecordEventRingBuffer.publish(next);
        return true;
    }
}
