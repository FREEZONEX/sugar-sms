package org.niiish32x.sugarsms.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PagaeDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDTO implements Serializable {
    private Integer total;
    private Integer pageSize;
    private Integer pageIndex;
}
