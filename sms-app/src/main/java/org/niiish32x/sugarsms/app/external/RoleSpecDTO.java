package org.niiish32x.sugarsms.app.external;

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

    @JSONField(name = "roleCode")
    private String roleCode;
    @JSONField(name = "roleName")
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
