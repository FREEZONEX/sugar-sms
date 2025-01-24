package org.niiish32x.sugarsms.user.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.user.UserDO;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;
import org.niiish32x.sugarsms.user.domain.entity.UserRoleEO;

import java.util.List;
import java.util.Map;

/**
 * UserConverter
 *
 * @author shenghao ni
 * @date 2025.01.24 13:53
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class,UserRoleEO.class})
public interface UserConverter {
    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mapping(target = "userRoleList" , expression = "java(JSON.toJSONString(userEO.getUserRoleList()))")
    UserDO toDO (UserEO userEO);

    @Mapping(source = "userRoleList" , target =  "userRoleList" , qualifiedByName = "parseUserRoleListToEO")
    UserEO toEO (UserDO userDO);

    @Named("parseUserRoleListToEO")
    default List<UserRoleEO> parseUserRoleListToEO(String userRoleList) {
        return JSON.parseArray(userRoleList,  UserRoleEO.class);
    }

}
