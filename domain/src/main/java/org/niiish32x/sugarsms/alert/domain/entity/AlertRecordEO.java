package org.niiish32x.sugarsms.alert.domain.entity;

/**
 * AlertRecordEO
 *
 * @author shenghao ni
 * @date 2024.12.16 17:46
 */
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRecordEO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 自增主键
    private Long id;
    // 报警消息ID
    private Long alertId;
    // 短信发送状态，0表示未完成，1表示已完成
    private Boolean smsSendStatus;
    // 短信全部通知到的时间
    private Date smsSendTime;
    // 邮件发送状态，0表示未完成，1表示已完成
    private Boolean emailSendStatus;
    // 邮件成功发送的时间
    private Date emailSendTime;
    // 是否全部发送成功状态
    private Boolean status;
    // 通知的具体内容
    private String content;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;

}