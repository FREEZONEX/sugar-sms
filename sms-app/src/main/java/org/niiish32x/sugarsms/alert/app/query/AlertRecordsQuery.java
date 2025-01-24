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
    private boolean status;



    private long page;
    private long limit;


    public AlertRecordsQuery( boolean status, long page, long limit) {

        this.status = status ;
        this.page = page;
        this.limit = limit;
    }
}
