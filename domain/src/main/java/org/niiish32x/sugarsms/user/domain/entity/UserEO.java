package org.niiish32x.sugarsms.user.domain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * UserEO
 *
 * @author shenghao ni
 * @date 2025.01.05 15:47
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEO {
    String username;
    String userDesc;
    Integer accountType;
    Integer lockStatus;
    Integer valid;
    String personCode;
    String personName;

    @JSONField(name = "userRoleList")
    List<UserRoleEO> userRoleList;

    String avatar;


    Date modifyTime;
    Date createTime;

}
