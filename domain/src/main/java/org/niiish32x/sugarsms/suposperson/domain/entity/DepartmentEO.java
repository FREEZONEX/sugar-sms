package org.niiish32x.sugarsms.suposperson.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DepartmentEO
 *
 * @author shenghao ni
 * @date 2025.01.08 17:56
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentEO implements Serializable {
    private String name;
    private String code;
}
