package org.niiish32x.sugarsms.suposperson.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PositionEO
 *
 * @author shenghao ni
 * @date 2025.01.08 17:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionEO implements Serializable {
    private String name;
    private String code;
}
