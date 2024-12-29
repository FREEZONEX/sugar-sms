package org.niiish32x.sugarsms.api.person.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * EducationDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:32
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationDTO implements Serializable {
    private String code;
    private String name;
}
