package org.niiish32x.sugarsms.app.proxy;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
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

    // ratelimiter 限流 smtp 有并发数限制
    private static final RateLimiter limiter = RateLimiter.create(10);


    /**
     * 发送简单文本邮件的方法
     *
     * @param from    发件人邮箱地址
     * @param to      收件人邮箱地址
     * @param password 发件人邮箱授权码
     * @param subject 邮件主题
     * @param content 邮件正文内容
     */
    public static void sendTextEmail(String from, String to, String password, String subject, String content) {
        // 设置邮件服务器相关属性
        Properties properties = new Properties();
        // 以QQ邮箱的SMTP服务器为例
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.password","L*112832568463od");
        properties.put("mail.smtp.starttls.enable", "true");

        // 获取邮件会话对象
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // 创建邮件消息对象
            MimeMessage message = new MimeMessage(session);
            // 设置发件人
            message.setFrom(new InternetAddress(from));
            // 设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 设置邮件主题
            message.setSubject(subject);
            // 设置邮件正文内容
            message.setText(content);

            // 发送邮件
            Transport.send(message);
            System.out.println("邮件发送成功！");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }

//    public static boolean sendTextEmail(String from, String to, String subject, String content) {
//
//        String password = "L*112832568463od";
//
//        // 设置邮件服务器相关属性
//        Properties properties = new Properties();
//        // 以QQ邮箱的SMTP服务器为例
//        properties.put("mail.smtp.host", "smtp.office365.com");
//        properties.put("mail.smtp.port", "25");
//        properties.put("mail.smtp.auth", "true");
//        properties.put("mail.smtp.password","L*112832568463od");
//        properties.put("mail.smtp.starttls.enable", "true");
//
//        // 获取邮件会话对象
//        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
//                return new javax.mail.PasswordAuthentication(from, password);
//            }
//        });
//
//        try {
//            // 创建邮件消息对象
//            MimeMessage message = new MimeMessage(session);
//            // 设置发件人
//            message.setFrom(new InternetAddress(from));
//            // 设置收件人
//            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            // 设置邮件主题
//            message.setSubject(subject);
//            // 设置邮件正文内容
//            message.setText(content);
//
//            // 发送邮件
//            Transport.send(message);
//            log.info("邮件发送成功! 通知邮箱 {}",to);
//
//            return true;
//        } catch (MessagingException e) {
//            e.printStackTrace();
//            log.error("{} 邮件发送失败 {}",to,e.getMessage());
//            return false;
//        }
//    }

    public boolean sendTextEmail(String from, String to, String subject, String content) {


        // 验证输入参数
        if (from == null || to == null || subject == null || content == null) {
            log.error("输入参数不能为空");
            return false;
        }

        limiter.acquire();

        // 设置邮件服务器相关属性
        Properties properties = new Properties();


        if (isValid(SMTP_HOST) && isValid(SMTP_PORT) && isValid(SMTP_AUTH) && isValid(SMTP_STARTTLS_ENABLE)) {
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", SMTP_AUTH);
            properties.put("mail.smtp.starttls.enable", SMTP_STARTTLS_ENABLE);
        } else {
            throw new IllegalArgumentException("Invalid SMTP configuration parameters");
        }



        // 获取邮件会话对象
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, SMTP_PASSWORD);
            }
        });

        try {
            // 创建邮件消息对象
            MimeMessage message = new MimeMessage(session);
            // 设置发件人
            message.setFrom(new InternetAddress(from));
            // 设置收件人
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // 设置邮件主题
            message.setSubject(subject);
            // 设置邮件正文内容
            message.setText(content);

            // 发送邮件
            Transport.send(message);
            log.info("邮件发送成功! 通知邮箱 {}", to);

            return true;
        } catch (MessagingException e) {
            log.error("邮件发送失败: {}, 错误信息: {}", to, e.getMessage());
            return false;
        }
    }


    private static boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
