package org.niiish32x.sugarsms.alert;

/**
 * AlertRecordDO
 *
 * @author shenghao ni
 * @date 2024.12.16 18:15
 */

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;


@Data
@TableName("alert_record")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    @TableField(value = "alert_id")
    private Long alertId;


    @TableField(value = "sms_send_status")
    private Integer smsSendStatus;

    @TableField(value = "sms_send_time")
    private Date smsSendTime;

    @TableField(value = "email_send_status")
    private Integer emailSendStatus;

    @TableField(value = "email_send_time")
    private Date emailSendTime;

    @TableField(value = "status")
    private Integer status;

    @TableField(value = "content")
    private String content;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}