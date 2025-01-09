package org.niiish32x.sugarsms.suposperson.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * PersonEO
 *
 * @author shenghao ni
 * @date 2025.01.08 17:47
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuposPersonEO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;

    private String code;
    private String name;
    private int valid;

    private GenderEO gender;
    private StatusEO status;
    private MainPositionEO mainPosition;
    private String entryDate;
    private TitleEO title;
    private String qualification;

    private EducationEO education;
    private String major;
    private String idNumber;
    private String phone;
    private String email;
    private String avatarUrl;
    private String signUrl;
    private List<DepartmentEO> departments;
    private List<CompanyEO> companies;
    private UserEO user;
    private List<PositionEO> positions;
    private Date modifyTime;

    private DirectLeaderEO directLeader;
    private GrandLeaderEO grandLeader;
    /**
     * 软删除标志位  0未删除 1表示删除
     */
    private Integer deleted;

    private Date createTime;

    private Date updateTime;
}
