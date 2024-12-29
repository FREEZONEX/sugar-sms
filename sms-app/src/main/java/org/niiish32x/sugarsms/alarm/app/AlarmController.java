package org.niiish32x.sugarsms.alarm.app;

import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AlarmController
 *
 * @author shenghao ni
 * @date 2024.12.29 10:40
 */

@RestController
public class AlarmController {
    @Autowired
    AlarmService alarmService;



    @RequestMapping("/alarm")
    public Result<List<AlarmDTO>>  getAlarmFromSupos(@RequestBody AlarmRequest request) {
        return alarmService.getAlarmsFromSupos(request);
    }

    @RequestMapping("/alarm/sync")
    public Result<Boolean>  syncAlarmFromSupos( ) {
        return alarmService.syncAlarmFromSupos();
    }
}
