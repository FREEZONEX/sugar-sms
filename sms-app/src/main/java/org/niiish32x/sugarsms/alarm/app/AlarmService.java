package org.niiish32x.sugarsms.alarm.app;

import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * AlarmService
 *
 * @author shenghao ni
 * @date 2024.12.29 10:11
 */
public interface AlarmService {
    Result<List<AlarmDTO>> getAlarmsFromSupos(AlarmRequest request);

    Result<Boolean> save(AlarmDTO alarmDTO);

    Result<Boolean> syncAlarmFromSupos();
}
