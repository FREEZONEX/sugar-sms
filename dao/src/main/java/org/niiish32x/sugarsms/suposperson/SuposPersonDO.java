package org.niiish32x.sugarsms.suposperson;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * PersonDO
 *
 * @author shenghao ni
 * @date 2024.12.19 18:17
 */

@Data
@TableName("supos_person")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SuposPersonDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "code")
    private String code;
    @TableField(value = "name")
    private String name;
    @TableField(value = "valid")
    private int valid;
    @TableField(value = "gender")
    private String gender;
    @TableField(value = "status;")
    private String status;
    @TableField(value = "mainPosition")
    private String mainPosition;
    @TableField(value = "entryDate")
    private String entryDate;
    @TableField(value = "title")
    private String title;
    @TableField(value = "qualification")
    private String qualification;
    @TableField(value = "education")
    private String education;
    @TableField(value = "major")
    private String major;
    // 身份证号
    @TableField(value = "idNumber")
    private String idNumber;
    @TableField(value = "phone")
    private String phone;
    @TableField(value = "email")
    private String email;
    @TableField(value = "avatarUrl")
    private String avatarUrl;
    @TableField(value = "signUrl")
    private String signUrl;
    @TableField(value = "departments")
    private String departments;
    @TableField(value = "companies")
    private String companies;
    // 用户相关信息 指的是该person所绑定的用户
    @TableField(value = "user")
    private String user;

    // 人员所在岗位信息
    @TableField(value = "positions")
    private String  positions;
    // 最后修改时间时间(零时区时间)，格式为：“yyyy-MM-dd’T’HH:mm:ss.SSSZ”,例如：“2021-01-26T16:02:15.666+0000”
    @TableField(value = "modifyTime")
    private Date modifyTime;

    // 直属领导
    @TableField(value = "directLeader")
    private String directLeader;

    // 隔级领导\
    @TableField(value = "grandLeader")
    private String grandLeader;

    /**
     * 软删除标志位  0未删除 1表示删除
     */
    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;

}
