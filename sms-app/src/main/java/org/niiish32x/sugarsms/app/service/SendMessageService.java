package org.niiish32x.sugarsms.app.service;

import io.github.biezhi.ome.SendMailException;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;
import java.util.Map;

/**
 * SendMessageService
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */
public interface SendMessageService {

    Result <ZubrixSmsResponse> sendOneZubrixSmsMessage(String number, String text);



    void sendEmail() ;

    void sendEmail(String toMail,String subject,String text) ;
}
