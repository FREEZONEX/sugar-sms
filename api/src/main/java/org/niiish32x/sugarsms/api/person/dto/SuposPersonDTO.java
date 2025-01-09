package org.niiish32x.sugarsms.api.person.dto;

/**
 * PersonDTO
 *
 * @author shenghao ni
 * @date 2024.12.08 18:25
 */
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuposPersonDTO implements Serializable {
    private String code;
    private String name;
    private int valid;

    @JSONField(name = "gender")
    private GenderDTO gender;

    @JSONField(name = "status")
    private StatusDTO status;

    @JSONField(name = "mainPosition")
    private MainPositionDTO mainPosition;
    private String entryDate;

    @JSONField(name = "title")
    private TitleDTO title;
    private String qualification;


    @JSONField(name = "education")
    private EducationDTO education;
    private String major;
    private String idNumber;
    private String phone;
    private String email;
    private String avatarUrl;
    private String signUrl;

    @JSONField(name = "departments")
    private List<DepartmentDTO> departments;

    @JSONField(name = "companies")
    private List<CompanyDTO> companies;

    @JSONField(name = "user")
    private SuposUserDTO user;

    @JSONField(name = "positions")
    private List<PositionDTO> positions;
    private Date modifyTime;

    @JSONField(name = "directLeader")
    private DirectLeaderDTO directLeader;


    @JSONField(name = "grandLeader")
    private GrandLeaderDTO grandLeader;

}