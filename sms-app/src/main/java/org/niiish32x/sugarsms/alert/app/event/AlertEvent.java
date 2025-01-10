package org.niiish32x.sugarsms.alert.app.event;

import lombok.Data;
import lombok.Getter;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * AlertEvent
 *
 * @author shenghao ni
 * @date 2024.12.15 14:17
 */

@Getter
public class AlertEvent extends ApplicationEvent {

    private List<AlertRecordEO> alertRecordEOS;

    public AlertEvent(Object source, List<AlertRecordEO> alertRecordEOS) {
        super(source);
        this.alertRecordEOS = alertRecordEOS;
    }
}
