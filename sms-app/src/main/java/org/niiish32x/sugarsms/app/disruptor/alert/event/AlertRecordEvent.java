package org.niiish32x.sugarsms.app.disruptor.alert.event;

/**
 * AlertRecordEvent
 *
 * @author shenghao ni
 * @date 2025.01.16 14:08
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;

/**
 * 生成 报警记录 事件
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRecordEvent {
   private AlertEO alertEO;
}
