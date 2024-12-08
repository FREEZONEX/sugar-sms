package org.niiish32x.sugarsms.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * TitleDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:34
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TitleDTO implements Serializable {
    private String code;
    private String name;

}
