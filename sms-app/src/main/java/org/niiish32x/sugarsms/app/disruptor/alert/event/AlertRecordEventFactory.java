package org.niiish32x.sugarsms.app.disruptor.alert.event;

import com.lmax.disruptor.EventFactory;

/**
 * AlertRecordEventFactory
 *
 * @author shenghao ni
 * @date 2025.01.16 14:16
 */
public class AlertRecordEventFactory implements EventFactory<AlertRecordEvent> {
    @Override
    public AlertRecordEvent newInstance() {
        return new AlertRecordEvent();
    }
}
