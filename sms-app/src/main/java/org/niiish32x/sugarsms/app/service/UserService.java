package org.niiish32x.sugarsms.app.service;


import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * UserService
 *
 * @author shenghao ni
 * @date 2024.12.08 13:25
 */


public interface UserService {



    Result addSuposUser(String username,String password);

    Result addSuposUser(String username,String password,List<String> roleNameList);

    Result mockUser();

    Result getUsersFromSupos(String company);

    Result<List<SuposUserDTO>>  getUsersFromSupos(String companyCode, String roleCode);

    /**
     * 为指定用户 绑定角色角色
     * @param username
     * @param role
     * @return
     */
    Result <SuposUserDTO> role(String username,String role);

    Result getMessageReceived(String username);

    Result<List<RoleSpecDTO>> getRoleListFromSupos(String companyCode);
}
