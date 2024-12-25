package org.niiish32x.sugarsms.app.controller;

import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.result.Result;
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
    public Result getCompanyUser(@RequestParam String companyCode) {
        return  suposUserService.getUsersFromSupos(StringUtils.isBlank(companyCode) ? "default_org_company" : companyCode);
    }

    @RequestMapping("/users/company/role")
    public Result getCompanyUserByRole(String companyCode,String roleCode) {
        return  suposUserService.getUsersFromSupos(StringUtils.isBlank(companyCode) ? "default_org_company" : companyCode,roleCode);
    }

    @PostMapping("/users/add")
    public Result getCompanyUser(@RequestParam String username,@RequestParam String password) {
        return  suposUserService.addSuposUser(username,password);
    }

    @RequestMapping("/users/message")
    public Result getUserMessage() {

        return  suposUserService.getMessageReceived("admin");
    }


    @RequestMapping("/users/mock")
    public Result mockCompanyUser() {

        return  suposUserService.mockUser();
    }

}
