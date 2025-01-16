package org.niiish32x.sugarsms.app.disruptor.alert.event;

import com.lmax.disruptor.EventFactory;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;

/**
 * EventFactory
 *
 * @author shenghao ni
 * @date 2025.01.16 10:34
 */
public class AlertMessgaeEventFactory implements EventFactory<AlertEvent> {
    @Override
    public AlertEvent newInstance() {
        return new AlertEvent();
    }
}
