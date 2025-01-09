package org.niiish32x.sugarsms.alarm.app.query;

import com.google.common.base.Preconditions;
import lombok.Getter;

/**
 * AlarmQuery
 *
 * @author shenghao ni
 * @date 2025.01.09 16:29
 */
@Getter
public class AlarmQuery {
    private  String attributeEnName;
    private  String alarmId;

    public AlarmQuery(String attributeEnName, String alarmId) {
        Preconditions.checkArgument((attributeEnName != null || alarmId != null), "attributeEnName or alarmId must not be null" );

        this.attributeEnName = attributeEnName;
        this.alarmId = alarmId;
    }

    public AlarmQuery(String attributeEnName) {
        Preconditions.checkArgument((attributeEnName != null ), "attributeEnName  must not be null" );
        this.attributeEnName = attributeEnName;
    }
}
