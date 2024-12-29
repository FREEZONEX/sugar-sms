package org.niiish32x.sugarsms.alert.app;

import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
        return alertService.getAlertsFromSupos();
    }

    @RequestMapping("/alert/notify/sugarsms")
    public Result notifySugarsms(){
        return null;
    }

    @RequestMapping("/alert/notify/sugarsms/test")
    public Result  <ZubrixSmsResponse>  notifyTest(){
        return alertService.notifyTest();
    }


    @RequestMapping("/alert/record/test")
    public List<AlertRecordEO> testAlert() {
        return alertService.getAllAlertRecords();
    }

}
