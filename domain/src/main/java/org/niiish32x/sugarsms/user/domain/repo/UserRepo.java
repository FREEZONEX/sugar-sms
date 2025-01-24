package org.niiish32x.sugarsms.user.domain.repo;

import org.niiish32x.sugarsms.user.domain.entity.UserEO;

import java.util.List;

/**
 * UserRepo
 *
 * @author shenghao ni
 * @date 2025.01.24 14:00
 */
public interface UserRepo {

    List<UserEO> findAll();

    boolean save(UserEO userEO);
}
