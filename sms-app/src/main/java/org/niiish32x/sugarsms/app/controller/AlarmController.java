package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.dto.AlarmDTO;
import org.niiish32x.sugarsms.app.service.AlarmService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * AlarmController
 *
 * @author shenghao ni
 * @date 2024.12.27 15:05
 */


@RestController
public class AlarmController {
    @Autowired
    AlarmService alarmService;


    @RequestMapping("/alarm")
    public Result<List<AlarmDTO>> getAlarmsFromSupos() {
        return alarmService.getAlarmsFromSupos();
    }
}
