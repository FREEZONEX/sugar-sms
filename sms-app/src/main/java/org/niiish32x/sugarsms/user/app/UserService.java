package org.niiish32x.sugarsms.user.app;


import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.user.app.external.UserPageQueryRequest;

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

    Result <List<SuposUserDTO>> getUsersFromSupos(UserPageQueryRequest request);



    Result<List<SuposUserDTO>>  getUsersFromSupos(String companyCode, String roleCode);




    Result<List<RoleSpecDTO>> getRoleListFromSupos(String companyCode);
}
