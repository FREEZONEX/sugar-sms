package org.niiish32x.sugarsms.api.person.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * CompanyDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public  class CompanyDTO implements Serializable {
    private String name;
    private String code;

}