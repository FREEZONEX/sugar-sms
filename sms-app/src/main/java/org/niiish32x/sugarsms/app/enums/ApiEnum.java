package org.niiish32x.sugarsms.app.enums;

/**
 * ApiEnum
 *
 * @author shenghao ni
 * @date 2024.12.08 18:22
 */
public enum ApiEnum {

    USER_API("/open-api/auth/v2/users"),

    PESRON_GET_API("/open-api/organization/v2/persons"),

    PESRON_POST_API("/open-api/organization/v2/persons/bulk"),

    MESSAGE_POST_API("/open-api/p/notification/v2/topic/messages"),

    ALERT_API("/open-api/supos/oodm/v2/alert/current"),

    SENDSMS_API("http://cloudsms.zubrixtechnologies.com/api/mt/SendSMS");

    public final String value;

    ApiEnum(String value) {
        this.value = value;
    }


}
