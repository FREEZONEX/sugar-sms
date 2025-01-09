package org.niiish32x.sugarsms.suposperson.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DirectLeaderEO
 *
 * @author shenghao ni
 * @date 2025.01.08 17:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DirectLeaderEO implements Serializable {
    private String code;
    private String name;
}
