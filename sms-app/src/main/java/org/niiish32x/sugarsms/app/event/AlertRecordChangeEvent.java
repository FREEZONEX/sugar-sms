package org.niiish32x.sugarsms.app.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * AlertRecordChangeEvent
 *
 * @author shenghao ni
 * @date 2025.01.22 16:05
 */


@Getter
public class AlertRecordChangeEvent extends ApplicationEvent {
    public AlertRecordChangeEvent(Object source) {
        super(source);
    }
}
