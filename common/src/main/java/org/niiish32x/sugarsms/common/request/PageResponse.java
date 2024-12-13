package org.niiish32x.sugarsms.common.request;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * PageResponse
 *
 * @author shenghao ni
 * @date 2024.12.08 18:49
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse implements Serializable {
    @JSONField(name = "pagination")
    Pagination pagination;

}
