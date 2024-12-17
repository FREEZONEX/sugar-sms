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

    @TableField(value = "type")
    private String type;

    @TableField(value = "send_time")
    private LocalDateTime sendTime;

    /**
     * 0 表示发送失败
     * 1 表示发送成功
     */
    @TableField(value = "status")
    private Integer status;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "email")
    private String email;

    @TableField(value = "content")
    private String content;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

}