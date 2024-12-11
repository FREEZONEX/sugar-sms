package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.common.supos.result.Result;

/**
 * SendMessageService
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */
public interface SendMessageService {
    void sendOneToSms();


    Result sendOneZubrixSms(String number, String text);

    void sendOneToSms(String number, String text);

    Result sendMessageToSugarSmsUser();

    Result sendMessageToSugarSmsUser(String text);

    Result sendAlertToSugarSmsUser();
}
