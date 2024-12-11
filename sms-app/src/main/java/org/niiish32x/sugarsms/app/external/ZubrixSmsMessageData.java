package org.niiish32x.sugarsms.app.external;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * SMSMessageData
 *
 * @author shenghao ni
 * @date 2024.12.10 11:27
 */

@Data
public class ZubrixSmsMessageData implements Serializable {
    @JSONField(name = "Number")
    private String number;
    @JSONField(name = "MessageId")
    private String messageId;

}
