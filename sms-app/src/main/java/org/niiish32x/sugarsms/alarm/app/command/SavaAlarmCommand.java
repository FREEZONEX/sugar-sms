package org.niiish32x.sugarsms.alarm.app.command;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;

import java.util.List;

/**
 * SavaAlarmCommand
 *
 * 保存报警信息 基本命令
 *
 * @author shenghao ni
 * @date 2024.12.29 22:56
 */

@Getter
@Slf4j
public class SavaAlarmCommand {
    private final AlarmDTO alarmDTO;

    public SavaAlarmCommand(AlarmDTO alarmDTO){
        if (alarmDTO == null) {
            throw new IllegalArgumentException("AlarmDTO cannot be null");
        }
        this.alarmDTO = alarmDTO;
        log.debug("SaveAlarmCommand initialized with alarmDTO: {}", alarmDTO);
    }
}
