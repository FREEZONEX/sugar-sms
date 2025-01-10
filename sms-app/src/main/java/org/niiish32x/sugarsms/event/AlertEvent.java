package org.niiish32x.sugarsms.event;

import org.springframework.context.ApplicationEvent;

/**
 * AlertEvent
 *
 * @author shenghao ni
 * @date 2024.12.15 14:17
 */
public class AlertEvent extends ApplicationEvent {

    public AlertEvent(Object source) {
        super(source);
    }
}
