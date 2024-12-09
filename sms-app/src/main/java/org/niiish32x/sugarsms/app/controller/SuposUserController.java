package org.niiish32x.sugarsms.app.controller;

import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @RequestMapping("/users/company")
    public List<SuposUserDTO> getCompanyUser(String companyCode) {

        return  suposUserService.getUsersFromSupos(StringUtils.isBlank(companyCode) ? "default_org_company" : companyCode);
    }

    @PostMapping("/users/company")
    public Result getCompanyUser(@RequestBody SuposUserDTO userDTO) {
        return  suposUserService.addSuposUser(userDTO);
    }
}
