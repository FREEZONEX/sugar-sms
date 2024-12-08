package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.dto.SuposUserDTO;

import java.util.List;

/**
 * UserService
 *
 * @author shenghao ni
 * @date 2024.12.08 13:25
 */


public interface SuposUserService {
    public List<SuposUserDTO> getSuposUsersFromSupos();

    public List<SuposUserDTO> getCompanyUsersFromSupos();
}
