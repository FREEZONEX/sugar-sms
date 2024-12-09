package org.niiish32x.sugarsms.app.service;

/**
 * SendMessageService
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */
public interface SendMessageService {
    void sendOne();

    void sendOne(String number,String text);

    void SendMessageToSugarSmsUser();
}
