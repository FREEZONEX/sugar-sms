package org.niiish32x.sugarsms.alert.app.command;

import lombok.Getter;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;

/**
 * AlertCommand
 *
 * @author shenghao ni
 * @date 2024.12.29 23:13
 */

@Getter
public class AlertCommand {
    private final SuposUserDTO userDTO;
    private final AlertInfoDTO alertInfoDTO;
    private final AlarmDTO alarmDTO;

    public AlertCommand(SuposUserDTO userDTO, AlertInfoDTO alertInfoDTO, AlarmDTO alarmDTO) {

        if (userDTO == null || alertInfoDTO == null || alarmDTO == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        this.userDTO = userDTO;
        this.alertInfoDTO = alertInfoDTO;
        this.alarmDTO = alarmDTO;
    }
}
