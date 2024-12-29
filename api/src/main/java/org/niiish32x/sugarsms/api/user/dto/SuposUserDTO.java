package org.niiish32x.sugarsms.api.user.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * UserDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 13:26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuposUserDTO implements Serializable {
    String username;
    String userDesc;
    Integer accountType;
    Integer lockStatus;
    Integer valid;
    String personCode;
    String personName;

    @JSONField(name = "userRoleList")
    List<SuposUserRoleDTO> userRoleList;

    String avatar;


    Date modifyTime;
    Date createTime;

}
