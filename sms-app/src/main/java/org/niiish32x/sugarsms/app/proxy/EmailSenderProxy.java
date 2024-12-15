package org.niiish32x.sugarsms.app.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
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
public class EmailSenderProxy {

    public static final String PUBLIC_EMAIL_SUGAR = "techalert@dhampursugar.com";


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
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(from, password);
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

    public static boolean sendTextEmail(String from, String to, String subject, String content) {

        String password = "L*112832568463od";

        // 设置邮件服务器相关属性
        Properties properties = new Properties();
        // 以QQ邮箱的SMTP服务器为例
        properties.put("mail.smtp.host", "smtp.office365.com");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.password","L*112832568463od");
        properties.put("mail.smtp.starttls.enable", "true");

        // 获取邮件会话对象
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(from, password);
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
            log.info("邮件发送成功! 通知邮箱 {}",to);

            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            log.error("{} 邮件发送失败 {}",to,e.getMessage());
            return false;
        }
    }
}
