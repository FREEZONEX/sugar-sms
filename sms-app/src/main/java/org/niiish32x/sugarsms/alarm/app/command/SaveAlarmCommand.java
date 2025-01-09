package org.niiish32x.sugarsms.alarm.app.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;

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
public class SaveAlarmCommand {
    private final AlarmDTO alarmDTO;

    public SaveAlarmCommand(AlarmDTO alarmDTO){
        Preconditions.checkArgument(alarmDTO != null, "alarmDTO cannot be null");
        this.alarmDTO = alarmDTO;
        log.debug("SaveAlarmCommand initialized with alarmDTO: {}", alarmDTO);
    }
}
