package org.niiish32x.sugarsms.api.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;

import java.util.List;
import java.util.TimeZone;

/**
 * SuposUserAddRequest
 *
 * @author shenghao ni
 * @date 2024.12.09 10:51
 */

@Data
public class SuposUserAddRequest {

    private String username;

    private String password;

    private String userDesc;

    private String timeZone;

    private String personCode;

    private  String companyCode;

    private Integer accountType;


    List<String> roloNameList;


    public SuposUserAddRequest(String username, String password) {
        this.username = username;
        this.password = password;
        this.timeZone = "GMT+0800";
        this.personCode = username;
        this.companyCode = CompanyEnum.DEFAULT.value;
        this.accountType =  0;
    }

    public SuposUserAddRequest(String username, String password,List <String> list) {
        this.username = username;
        this.password = password;
        this.timeZone = "GMT+0800";
        this.personCode = username;
        this.companyCode = CompanyEnum.DEFAULT.value;
        this.accountType =  0;
        this.roloNameList = list;
    }
}
