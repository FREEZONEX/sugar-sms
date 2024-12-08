package org.niiish32x.sugarsms.app.dto;

import lombok.Data;

/**
 * PagaeDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:12
 */
@Data
public class PageDTO {
    private Integer total;
    private Integer pageSize;
    private Integer pageIndex;
}
