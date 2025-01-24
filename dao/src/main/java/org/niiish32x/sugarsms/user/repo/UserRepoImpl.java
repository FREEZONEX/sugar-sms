package org.niiish32x.sugarsms.user.repo;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.niiish32x.sugarsms.suposperson.SuposPersonDO;
import org.niiish32x.sugarsms.user.UserDO;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;
import org.niiish32x.sugarsms.user.domain.repo.UserRepo;
import org.niiish32x.sugarsms.user.persistence.converter.UserConverter;
import org.niiish32x.sugarsms.user.persistence.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserRepoImpl
 *
 * @author shenghao ni
 * @date 2025.01.24 14:00
 */
public class UserRepoImpl implements UserRepo {

    @Autowired
    UserDAO userDAO;


    UserConverter converter = UserConverter.INSTANCE;

    @Override
    public List<UserEO> findAll() {
        List<UserDO> list = userDAO.lambdaQuery().list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public boolean save(UserEO userEO) {
        UserDO userDO = converter.toDO(userEO);

        UserDO exist = userDAO.lambdaQuery()
                .eq(UserDO::getPersonCode, userDO.getPersonCode())
                .eq(UserDO::getPersonName, userDO.getPersonName())
                .eq(UserDO::getUsername, userDO.getUsername())
                .one();

        if (exist != null) {
            LambdaUpdateWrapper<UserDO>  wrapper = Wrappers.<UserDO>lambdaUpdate()
                    .set(UserDO::getUserRoleList, userDO.getUserRoleList())
                    .set(UserDO::getAvatar, userDO.getAvatar())
                    .set(UserDO::getModifyTime, userDO.getModifyTime())
                    .set(UserDO::getCreateTime, userDO.getCreateTime())
                    .set(UserDO::getUserDesc, userDO.getUserDesc())
                    .set(UserDO::getAccountType, userDO.getAccountType())
                    .set(UserDO::getLockStatus, userDO.getLockStatus())
                    .set(UserDO::getValid, userDO.getValid());
            return userDAO.update(wrapper);
        }


        return userDAO.save(userDO);
    }
}
