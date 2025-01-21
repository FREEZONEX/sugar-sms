package org.niiish32x.sugarsms.api.alert.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AlertAckResponse
 *
 * @author shenghao ni
 * @date 2025.01.21 15:32
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertAckResponse implements Serializable {
    @JSONField(name = "code")
    Integer code;

    @JSONField(name = "detail")
    String detail;

    @JSONField(name = "message")
    String message;

    @JSONField(name = "targetService")
    String targetService;

}
