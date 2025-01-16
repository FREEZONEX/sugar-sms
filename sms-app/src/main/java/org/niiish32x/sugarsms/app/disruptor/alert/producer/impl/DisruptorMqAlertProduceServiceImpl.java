package org.niiish32x.sugarsms.app.disruptor.alert.producer.impl;

import com.lmax.disruptor.RingBuffer;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.producer.DisruptorMqAlertProduceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private RingBuffer<AlertEvent> ringBuffer;


    @Autowired
    private RingBuffer<AlertRecordEvent> alertRecordEventRingBuffer;

    public void produce(String text) {

        long next = ringBuffer.next();

        try {
            AlertEvent message = ringBuffer.get(next);
            message.setMessage(text);
        }finally {
            ringBuffer.publish(next);
        }

    }


    @Override
    public boolean produce(AlertEvent alertEvent) {
        return false;
    }

    @Override
    public boolean produceAlertRecordEvent(AlertRecordEvent alertRecordEvent) {
        long next = alertRecordEventRingBuffer.next();

        try {
            AlertRecordEvent event = alertRecordEventRingBuffer.get(next);
            event.setAlertInfoDTO(alertRecordEvent.getAlertInfoDTO());
        }catch (Exception e){
            log.error("produce AlertRecordEvent error");
            return false;
        }

        alertRecordEventRingBuffer.publish(next);
        return true;
    }
}
