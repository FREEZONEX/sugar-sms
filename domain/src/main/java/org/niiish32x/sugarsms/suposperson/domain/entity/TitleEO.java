package org.niiish32x.sugarsms.suposperson.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TitleEO
 *
 * @author shenghao ni
 * @date 2025.01.08 17:55
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TitleEO implements Serializable {
    private String code;
    private String name;
}
