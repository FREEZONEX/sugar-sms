package org.niiish32x.sugarsms.user.persistence.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.niiish32x.sugarsms.user.UserDO;
import org.niiish32x.sugarsms.user.persistence.mapper.UserMapper;
import org.springframework.stereotype.Component;

/**
 * UserDAOImpl
 *
 * @author shenghao ni
 * @date 2025.01.23 17:00
 */
@Component
public class UserDAOImpl extends ServiceImpl<UserMapper, UserDO> implements UserDAO {
}
