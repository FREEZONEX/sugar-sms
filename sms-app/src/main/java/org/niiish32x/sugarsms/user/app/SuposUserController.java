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

    @RequestMapping("/users/company")
    public Result getCompanyUser(@RequestBody UserPageQueryRequest request) {
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




    @RequestMapping("/users/mock")
    public Result mockCompanyUser() {

        return  suposUserService.mockUser();
    }

}
