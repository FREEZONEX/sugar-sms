package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.dto.AlarmDTO;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * AlarmService
 *
 * @author shenghao ni
 * @date 2024.12.27 15:01
 */
public interface AlarmService {
    Result<List<AlarmDTO>> getAlarmsFromSupos(String attributeEnName);

    Result<List<AlarmDTO>> getAlarmsFromSupos();

}
