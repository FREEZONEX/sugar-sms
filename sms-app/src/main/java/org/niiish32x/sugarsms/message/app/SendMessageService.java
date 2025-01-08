package org.niiish32x.sugarsms.message.app;

import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.common.result.Result;

/**
 * SendMessageService
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */
public interface SendMessageService {

    Result <ZubrixSmsResponse> sendOneZubrixSmsMessage(String number, String text);



    boolean sendEmail() ;

    boolean sendEmail(String toMail,String subject,String text) ;
}
