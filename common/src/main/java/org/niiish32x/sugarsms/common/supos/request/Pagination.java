package org.niiish32x.sugarsms.common.supos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Page
 *
 * @author shenghao ni
 * @date 2024.12.08 18:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagination implements Serializable {
    private Integer total;
    private Integer pageSize;
    private Integer current;
}
