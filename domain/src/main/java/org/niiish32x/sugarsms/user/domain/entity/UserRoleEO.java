package org.niiish32x.sugarsms.user.domain.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * UserRoleEO
 *
 * @author shenghao ni
 * @date 2025.01.05 15:48
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRoleEO implements Serializable {
    Integer total;
    String name;

    String showName;
    String description;
}
