package org.niiish32x.sugarsms.alert.domain.entity;

/**
 * MessageType
 *
 * @author shenghao ni
 * @date 2024.12.17 13:44
 */
public enum MessageType {
    EMAIL("email"),
    SMS("sms");

    String value;

    MessageType(String value) {
        this.value = value;
    }

}
