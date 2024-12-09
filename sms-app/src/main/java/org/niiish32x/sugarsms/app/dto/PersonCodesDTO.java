package org.niiish32x.sugarsms.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * PersonCodesDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 19:51
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonCodesDTO implements Serializable {
    List<String> personCodes;
}
