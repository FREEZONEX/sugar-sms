package org.niiish32x.sugarsms.alert.app;

import javafx.scene.control.Alert;
import org.niiish32x.sugarsms.alert.app.command.ProduceAlertRecordCommand;
import org.niiish32x.sugarsms.alert.app.command.SaveAlertCommand;
import org.niiish32x.sugarsms.alert.app.query.AlertRecordsCountQuery;
import org.niiish32x.sugarsms.alert.app.query.AlertRecordsQuery;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertRecordDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.common.result.PageResult;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * AlertService
 *
 * @author shenghao ni
 * @date 2024.12.10 9:56
 */
public interface AlertService {

     Result<List<AlertInfoDTO>> getAlertsFromSupos();

     Result <Boolean> saveAlert(SaveAlertCommand command);

     Result<List<SuposUserDTO>>  getAlertUsers();


     Boolean cleanAlertPastDays(Integer days);


     boolean productAlertRecord(AlertEO alertEO);

     /**
      * 生成 初始化 需要发送的报警消息
      * @return
      */
     Result productAlertRecord(ProduceAlertRecordCommand alertInfoDTOS);

     Result <List<AlertRecordDTO>> queryAlertRecords();

     // 确认 实时报警信息
     Result  ackAlerts();

     Result <PageResult<AlertRecordDTO>> searchAlertRecord(AlertRecordsQuery query);

     Result<Long> countAlertRecords(AlertRecordsCountQuery query);
}
