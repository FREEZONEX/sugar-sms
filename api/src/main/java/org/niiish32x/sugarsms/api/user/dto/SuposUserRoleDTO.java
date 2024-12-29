package org.niiish32x.sugarsms.api.user.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * SuposUserRole
 *
 * @author shenghao ni
 * @date 2024.12.08 13:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuposUserRoleDTO implements Serializable {
    Integer total;
    String name;
    @JSONField(name = "showname")
    String showName;
    String description;

}
