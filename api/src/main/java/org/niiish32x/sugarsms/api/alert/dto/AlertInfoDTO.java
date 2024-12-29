package org.niiish32x.sugarsms.api.alert.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

/**
 * AlertMessageDTO
 *
 * @author shenghao ni
 * @date 2024.12.10 9:57
 */

@Data
public class AlertInfoDTO {
    private long id;
    private String fullName;
    private int status;
    @JSONField(name = "startDatatimestamp")
    private long startDataTimestamp;
    @JSONField(name = "disappearedDatatimestamp")
    private Long disappearedDataTimestamp;
    private int ackStatus;
    @JSONField(name = "ackDatatimestamp")
    private Long ackDataTimestamp;
    private String ackUserName;
    private String alertName;
    private String showName;
    private String alertType;
    private int priority;
    private String source;
    private String sourceShowName;
    private String sourcePropertyName;
    private String sourcePropShowName;
    private String description;
    private String newValue;
    private int valType;
    private String oldValue;
}
