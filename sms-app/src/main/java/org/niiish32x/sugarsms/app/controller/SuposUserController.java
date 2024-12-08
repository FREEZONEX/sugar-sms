package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.service.SuposUserService;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
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
    SuposUserService suposUserService;

    @RequestMapping("/test/user")
    public List<SuposUserDTO> testUser() {
        System.out.println("xxxxxxxxxxxxxx");
        return  suposUserService.getUsersFromSupos("default_org_company");
    }
}
