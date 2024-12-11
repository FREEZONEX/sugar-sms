package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.common.supos.result.Result;

import java.util.List;

/**
 * AlertService
 *
 * @author shenghao ni
 * @date 2024.12.10 9:56
 */
public interface AlertService {
     Result<List<AlertInfoDTO>> getAlertsFromSupos();

     Result notifySugarSmsUser();

     Result  <ZubrixSmsResponse> notifyTest();
}
