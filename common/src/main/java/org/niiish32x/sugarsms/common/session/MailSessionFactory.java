package org.niiish32x.sugarsms.common.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.Properties;
import javax.mail.Session;
/**
 * SessionFactory
 *
 * @author shenghao ni
 * @date 2024.12.27 11:31
 */

@Component
public class MailSessionFactory {
    private final Properties properties;

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

    public MailSessionFactory() {
        properties = new Properties();
    }

    public MailSessionFactory(Properties properties, String from, String smtpPassword) {
        this.properties = properties;
    }

    public Session createMailSession() {

        if (isValid(SMTP_HOST) && isValid(SMTP_PORT) && isValid(SMTP_AUTH) && isValid(SMTP_STARTTLS_ENABLE)) {
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", SMTP_AUTH);
            properties.put("mail.smtp.starttls.enable", SMTP_STARTTLS_ENABLE);
        } else {
            throw new IllegalArgumentException("Invalid SMTP configuration parameters");
        }
        return Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(PUBLIC_EMAIL_SUGAR, SMTP_PASSWORD);
            }
        });
    }

    private static boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}