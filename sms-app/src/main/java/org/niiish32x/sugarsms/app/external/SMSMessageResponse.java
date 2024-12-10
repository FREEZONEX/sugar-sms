package org.niiish32x.sugarsms.app.external;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * SMSMessageResponse
 *
 * @author shenghao ni
 * @date 2024.12.10 11:26
 */

@Data
public class SMSMessageResponse implements Serializable {
    @JSONField(name = "ErrorCode")
    private Integer errorCode;
    @JSONField(name = "ErrorMessage")
    private String errorMessage;
    @JSONField(name = "JobId")
    private String jobId;
    @JSONField(name = "MessageData")
    private SMSMessageData messageData;
}
