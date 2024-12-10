package org.niiish32x.sugarsms.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * MessageDTO
 *
 * @author shenghao ni
 * @date 2024.12.09 18:45
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO implements Serializable {
    private String sender;
    private String topic;
    private String param;
    private String staffCode;
    private String staffName;
    private int sendStatus;
    private int readStatus;
    private String noticeProtocol;
    private Object errorResult;
}
