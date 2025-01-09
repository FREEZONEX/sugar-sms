package org.niiish32x.sugarsms.api.person.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DepartmentDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:27
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentDTO implements Serializable {
    private String name;
    private String code;

}
