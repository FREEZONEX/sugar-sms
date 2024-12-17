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

    private MessageType type;

    private LocalDateTime sendTime;

    private Boolean status;

    private String phone;

    private String email;

    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}