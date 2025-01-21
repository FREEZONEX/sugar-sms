package org.niiish32x.sugarsms.app.disruptor.alert.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;

/**
 * AlertEvent
 *
 * @author shenghao ni
 * @date 2025.01.16 10:33
 */

/**
 * 发送 报警事件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEvent {
    // 这里id 是记录的Id 即主键Id
    private Long alertRecordId;
}
