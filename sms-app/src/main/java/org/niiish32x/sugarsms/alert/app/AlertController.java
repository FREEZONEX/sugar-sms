package org.niiish32x.sugarsms.alert.app;

import org.niiish32x.sugarsms.alert.app.query.AlertRecordsQuery;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.api.alert.dto.AlertRecordDTO;
import org.niiish32x.sugarsms.common.result.PageResult;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping("/alerts/records")
    public Result<List<AlertRecordDTO>> getAlertRecords() {
        return alertService.queryAlertRecords();
    }

    @RequestMapping("/alerts/records/search")
    public Result<PageResult<AlertRecordDTO>> searchAlertRecord(
            @RequestParam(name = "page" , required = false,defaultValue = "1") long page,
            @RequestParam(name = "limit", required = false,defaultValue = "10") long limit
    ) {
        return alertService.searchAlertRecord(new AlertRecordsQuery(page, limit));
    }

    @RequestMapping("/alerts")
    public Result getAlert() {
        return alertService.getAlertsFromSupos();
    }

    @PostMapping("/alerts/ack")
    public Result ackAlert() {
        return alertService.ackAlerts();
    }

}
