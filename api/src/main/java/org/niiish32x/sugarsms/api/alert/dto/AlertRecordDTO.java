package org.niiish32x.sugarsms.api.alert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;

import java.util.Date;

/**
 * AlertRecordDTO
 *
 * @author shenghao ni
 * @date 2025.01.21 14:07
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRecordDTO {

    private static final long serialVersionUID = 1L;

    // 自增主键
    private Long id;
    // 报警消息ID
    private Long alertId;

    private Long alarmId;

//    private AlarmDTO alarm;

    private String type;

    private Date sendTime;

    private String username;

    private Boolean status;

    private String phone;

    private String email;

    private String content;

//    private Date createTime;
//
//    private Date updateTime;

    private Boolean expire;
}
