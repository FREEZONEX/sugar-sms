package org.niiish32x.sugarsms.user;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.niiish32x.sugarsms.user.domain.entity.UserRoleEO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * UserDO
 *
 * @author shenghao ni
 * @date 2025.01.23 16:51
 */
@Data
@TableName("user")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "username")
    String username;
    @TableField(value = "user_desc")
    String userDesc;
    @TableField(value = "account_type")
    Integer accountType;
    @TableField(value = "lock_status")
    Integer lockStatus;
    @TableField(value = "valid")
    Integer valid;
    @TableField(value = "person_code")
    String personCode;
    @TableField(value = "person_name")
    String personName;

    @TableField(value = "user_role_list")
    String userRoleList;

    @TableField(value = "avatar")
    String avatar;

    @TableField(value = "modify_time")
    Date modifyTime;
    @TableField(value = "create_time")
    Date createTime;
}
