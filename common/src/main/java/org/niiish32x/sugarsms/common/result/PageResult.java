package org.niiish32x.sugarsms.common.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * PageResult
 *
 * @author shenghao ni
 * @date 2025.01.22 14:19
 */
@Data
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = -6154358383429351056L;

    private long count;

    private List<T> list;

    private PageResult(long count, List<T> list) {
        this.count = count;
        this.list = list;
    }

    public static <T> PageResult<T> of(Long count, List<T> list){
        return new PageResult<>(count, list);
    }

}
