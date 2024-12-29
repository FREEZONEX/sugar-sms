package org.niiish32x.sugarsms.alarm.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InstanceLabelEO
 *
 * @author shenghao ni
 * @date 2024.12.29 18:04
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceLabelEO {
    String displayName;
    String id;
    String enName;
}
