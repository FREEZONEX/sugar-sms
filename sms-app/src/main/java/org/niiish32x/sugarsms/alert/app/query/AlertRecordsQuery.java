package org.niiish32x.sugarsms.alert.app.query;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;

/**
 * AlertRecordsQuery
 *
 * @author shenghao ni
 * @date 2025.01.21 14:02
 */

@Getter
public class AlertRecordsQuery {
    private long page;
    private long limit;


    public AlertRecordsQuery(long page, long limit) {
        this.page = page;
        this.limit = limit;
    }
}
