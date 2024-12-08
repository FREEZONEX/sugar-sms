package org.niiish32x.sugarsms.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PositionDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PositionDTO implements Serializable {
    private String name;
    private String code;
}
