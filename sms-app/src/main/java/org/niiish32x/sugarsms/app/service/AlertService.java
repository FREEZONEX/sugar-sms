package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * AlertService
 *
 * @author shenghao ni
 * @date 2024.12.10 9:56
 */
public interface AlertService {

     List<AlertRecordEO> getAllAlerts();


     Result<List<AlertInfoDTO>> getAlertsFromSupos();

     Result notifySugarUserBySms();

     Result notifySugarUserByEmail();

     Result  <ZubrixSmsResponse> notifyTest();

     void publishAlertEvent();

     /**
      * @param userDTO
      * @param alertInfoDTO
      * @return
      * 发送失败 则返回 发送失败邮箱
      */
     Result <String> notifyUserByEmail(SuposUserDTO userDTO,AlertInfoDTO alertInfoDTO);

     /**
      *
      * @param userDTO
      * @param alertInfoDTO
      * @return
      *
      * 发送失败则返回 发送失败的电话号码
      */
     Result <String> notifyUserBySms(SuposUserDTO userDTO,AlertInfoDTO alertInfoDTO);

     void consumeAlertEvent();
}
