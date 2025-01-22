package org.niiish32x.sugarsms.alert.app.query;

import lombok.Getter;

/**
 * AlertRecordsCountQuery
 *
 * @author shenghao ni
 * @date 2025.01.22 16:41
 */
@Getter
public class AlertRecordsCountQuery {
    private  Boolean total;
    private  Boolean status;

    public AlertRecordsCountQuery(Boolean total, Boolean status) {
        this.total = total;
        this.status = status;
    }
}
