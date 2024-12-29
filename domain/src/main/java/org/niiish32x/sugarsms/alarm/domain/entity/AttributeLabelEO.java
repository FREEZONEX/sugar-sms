package org.niiish32x.sugarsms.alarm.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AttributeLabelEO
 *
 * @author shenghao ni
 * @date 2024.12.29 18:04
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeLabelEO implements Serializable {
    private String displayName;
    private String id;
    private String enName;
}
