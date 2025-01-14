package org.niiish32x.sugarsms.common.enums;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * UserRoleEnum
 *
 * @author shenghao ni
 * @date 2025.01.05 15:50
 */
public enum UserRoleEnum {

    ADMIN("systemRole"),
    NORMAL("normalRole"),
    SUGAR("sugarsms"),
    COGEN("cogensms"),
    CHEMICAL("chemicalsms"),
    DISTILLERY("distillerysms"),
    COUNTRY_LIQOUR("CLsms"),
    MGMT("mgmtsms"),
    COMMON("commonsms")
    ;



    public final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }


    public static final Set ALERT_ROLE_SETS = Sets.newHashSet(UserRoleEnum.SUGAR.value, UserRoleEnum.COGEN.value, UserRoleEnum.CHEMICAL.value, UserRoleEnum.DISTILLERY.value, UserRoleEnum.COUNTRY_LIQOUR.value);

    public static boolean isAlertRole(UserRoleEnum role) {
        return ALERT_ROLE_SETS.contains(role);
    }

    public static boolean isAlertRole(String role) {
        try {

            return ALERT_ROLE_SETS.contains(role);
        } catch (IllegalArgumentException e) {
            return false;
        }

    }
}
