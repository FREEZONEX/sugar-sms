package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AlertController
 *
 * @author shenghao ni
 * @date 2024.12.10 10:12
 */

@RestController
public class AlertController {

    @Resource
    AlertService alertService;

    @RequestMapping("/alert")
    public Result getAlert() {
        return alertService.getAlerts();
    }
}
