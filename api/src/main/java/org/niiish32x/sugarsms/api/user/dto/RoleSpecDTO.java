package org.niiish32x.sugarsms.api.user.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * RoleSpecDTO
 *
 * @author shenghao ni
 * @date 2024.12.25 16:31
 */

@Data
public class RoleSpecDTO {

//    @JSONField(name = "roleCode")
    @JSONField(name = "code") // 印度环境为旧环境  roleCode 对应字段为 code
    private String roleCode;
//    @JSONField(name = "roleName")
    @JSONField(name = "name") // 印度环境为旧环境  roleName 对应字段为 name
    private String roleName;
    @JSONField(name = "description")
    private String description;

    @JSONField(name = "createTime")
    private Date createTime;

    @JSONField(name = "modifyTime")
    private Date modifyTime;

    @JSONField (name = "valid")
    private Integer valid;
}
