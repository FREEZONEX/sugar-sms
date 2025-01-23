package org.niiish32x.sugarsms.user.app;

import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.user.app.UserService;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.user.app.external.UserPageQueryRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * SuposUserController
 *
 * @author shenghao ni
 * @date 2024.12.08 13:39
 */

@RestController
public class SuposUserController {
    @Resource
    UserService suposUserService;

    @RequestMapping("/users/roleList")
    public Result getUserRoleList(@RequestParam String companyCode) {
        return  suposUserService.getRoleListFromSupos(companyCode);
    }

    @RequestMapping("/users")
    public Result getUserFromSupos(
            @RequestParam (name =  "keyword" , required = false )  String keyword,
            @RequestParam (name =  "roleCode" , required = false )  String roleCode,
            @RequestParam (name =  "companyCode" , required = true, defaultValue = "default_org_company")  String companyCode,
            @RequestParam (name =  "pageIndex" , required = false , defaultValue = "1")  Integer pageIndex,
            @RequestParam (name =  "pageSize" , required = false , defaultValue = "10")  Integer pageSize,
            @RequestParam (name =  "getAll" , required = false , defaultValue = "false")  Boolean getAll
            ) {
        UserPageQueryRequest request = new UserPageQueryRequest(
                keyword,
                pageIndex,
                pageSize,
                roleCode,
                companyCode,
                getAll
        );
        return  suposUserService.getUsersFromSupos(request);
    }

    @RequestMapping("/users/company/role")
    public Result getCompanyUserByRole(String roleCode) {
        UserPageQueryRequest request = UserPageQueryRequest.builder()
                .companyCode(CompanyEnum.DEFAULT.value)
                .roleCode(roleCode)
                .build();
        return  suposUserService.getUsersFromSupos(request);
    }

    @PostMapping("/users/add")
    public Result getCompanyUser(@RequestParam String username,@RequestParam String password) {
        return  suposUserService.addSuposUser(username,password);
    }

}
