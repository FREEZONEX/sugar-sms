package org.niiish32x.sugarsms.app.proxy;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.common.session.SessionPool;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * EmailSenderProxy
 *
 * @author shenghao ni
 * @date 2024.12.13 10:21
 */


@Slf4j
@Component
public class EmailSenderProxy {
    @Autowired
    private SessionPool sessionPool;


    @Value("${mail.smtp.host}")
    private String SMTP_HOST ;
    @Value("${mail.smtp.port}")
    private String SMTP_PORT ;
    @Value("${mail.smtp.auth}")
    private  String SMTP_AUTH ;
    @Value("${mail.smtp.password}")
    private  String SMTP_PASSWORD ;
    @Value("${mail.smtp.starttls.enable}")
    private  String SMTP_STARTTLS_ENABLE;

    public static final String PUBLIC_EMAIL_SUGAR = "techalert@dhampursugar.com";


    public boolean sendTextEmail(String from, String to, String subject, String content) {

        Session session = null;

        try {
            session = sessionPool.borrowMailSession();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}, 错误信息: {}", to, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("session 获取异常 {}",e.getMessage());
            return false;
        } finally {
            if (session != null) {
                sessionPool.returnSession(session);
            }

        }

        return true;
    }
}
