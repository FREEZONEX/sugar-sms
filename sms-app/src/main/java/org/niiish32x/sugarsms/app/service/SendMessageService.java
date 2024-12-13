package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.common.result.Result;

/**
 * SendMessageService
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */
public interface SendMessageService {

    Result <ZubrixSmsResponse> sendOneZubrixSms(String number, String text);
}
