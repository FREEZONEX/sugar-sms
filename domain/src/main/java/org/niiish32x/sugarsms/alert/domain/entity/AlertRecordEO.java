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
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;

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

    private Long alarmId;

    private AlarmEO alarm;

    private MessageType type;

    private Date sendTime;

    private String username;

    private Boolean status;

    private String phone;

    private String email;

    private String content;

    private Date createTime;

    private Date updateTime;

}