package org.niiish32x.sugarsms.common.enums;

/**
 * ApiEnum
 *
 * @author shenghao ni
 * @date 2024.12.08 18:22
 */
public enum ApiEnum {

    USER_PAGE_GET_API("/open-api/auth/v2/users"),

    // https://cloud.supos.com/open/APIs/detail?treeNodeCode=1521&openapiVersionCode=c3bc97b89d05458d8cef7ee0c810dd6b&apisManageCode=25353e4b1c614159ab4ffc3f9ddbb769&clickActive=supOS
    USER_ROLE_GET_API("/open-api/rbac/v2/roles"),

    PERSON_GET_API("/open-api/organization/v2/persons"),

    PERSON_BATCH_POST_API("/open-api/organization/v2/persons/bulk"),

    // 修改人员信息
    PERSON_MODIFY_API ("/open-api/supos/organization/v2.5/persons"),

    MESSAGE_POST_API("/open-api/p/notification/v2/topic/messages"),

    // 当前所有报警信息
    ALERT_API("/open-api/supos/oodm/v2/alert/current"),


    // 当前所有报警信息的详细值
    // https://cloud.supos.com/open/APIs/detail?treeNodeCode=1392&openapiVersionCode=c3bc97b89d05458d8cef7ee0c810dd6b&apisManageCode=25353e4b1c614159ab4ffc3f9ddbb769&clickActive=supOS
    ALARM_API("/open-api/supos/oodm/v2/alarms");

    public final String value;

    ApiEnum(String value) {
        this.value = value;
    }


}