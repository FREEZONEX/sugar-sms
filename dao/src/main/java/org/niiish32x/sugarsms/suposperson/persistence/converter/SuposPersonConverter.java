package org.niiish32x.sugarsms.suposperson.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import org.niiish32x.sugarsms.suposperson.SuposPersonDO;
import org.niiish32x.sugarsms.suposperson.domain.entity.*;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;


import java.util.List;
import java.util.Map;

/**
 * SuposPersonConverter
 *
 * @author shenghao ni
 * @date 2025.01.08 17:45
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface SuposPersonConverter {
    SuposPersonConverter INSTANCE = Mappers.getMapper(SuposPersonConverter.class);

    @Mapping(target = "gender",expression = "java(JSON.toJSONString(suposPersonEO.getGender()))")
    @Mapping(target = "status",expression = "java(JSON.toJSONString(suposPersonEO.getStatus()))")
    @Mapping(target = "mainPosition", expression = "java(JSON.toJSONString(suposPersonEO.getMainPosition()))")
    @Mapping(target = "title", expression = "java(JSON.toJSONString(suposPersonEO.getTitle()))")
    @Mapping(target = "education", expression = "java(JSON.toJSONString(suposPersonEO.getEducation()))")
    @Mapping(target = "departments" , expression = "java(JSON.toJSONString(suposPersonEO.getDepartments()))")
    @Mapping(target = "companies", expression = "java(JSON.toJSONString(suposPersonEO.getCompanies()))")
    @Mapping(target = "user" , expression = "java(JSON.toJSONString(suposPersonEO.getUser()))")
    @Mapping(target = "positions" , expression = "java(JSON.toJSONString(suposPersonEO.getPositions()))")
    @Mapping(target = "directLeader", expression = "java(JSON.toJSONString(suposPersonEO.getDirectLeader()))")
    @Mapping(target = "grandLeader", expression = "java(JSON.toJSONString(suposPersonEO.getGrandLeader()))")
    @Mapping(target = "deleted", expression = "java(suposPersonEO.getDeleted()  ? 1 : 0)")
    SuposPersonDO toDO (SuposPersonEO suposPersonEO) ;


    @Mapping(target = "gender",source = "gender" , qualifiedByName = "parseGenderToEO")
    @Mapping(target = "status",source = "status" , qualifiedByName = "parseStatusToEO")
    @Mapping(target = "mainPosition",  source = "mainPosition",qualifiedByName = "parseMainPositionToEO")
    @Mapping(target = "title", source = "title" , qualifiedByName = "parseTitleToEO")
    @Mapping(target = "education",  source = "education",qualifiedByName = "parseEducationToEO")
    @Mapping(target = "departments", source = "departments", qualifiedByName = "parseDepartmentsToEO")
    @Mapping(target = "companies" , source = "companies", qualifiedByName = "parseCompaniesToEO")
    @Mapping(target = "user" , source = "user",qualifiedByName = "parseUserToEO")
    @Mapping(target = "positions" , source = "positions", qualifiedByName = "parsePositionsToEO")
    @Mapping(target = "directLeader",  source = "directLeader", qualifiedByName = "parseDirectLeaderToEO")
    @Mapping(target = "grandLeader", source = "grandLeader", qualifiedByName = "parseGrandLeaderToEO")
    @Mapping(target = "deleted", expression = "java(suposPersonDO.getDeleted() == 0 ? false : true)")
    SuposPersonEO toEO (SuposPersonDO suposPersonDO);

    @Named("parseGenderToEO")
    default GenderEO parseGenderToEO(String gender) {
        return JSON.parseObject(gender, GenderEO.class);
    }

    @Named("parseStatusToEO")
    default StatusEO parseStatusToEO(String status) {
        return JSON.parseObject(status, StatusEO.class);
    }

    @Named("parseMainPositionToEO")
    default MainPositionEO parseMainPositionToEO(String mainPosition) {
        return JSON.parseObject(mainPosition, MainPositionEO.class);
    }

    @Named("parseTitleToEO")
    default TitleEO parseTitleToEO(String title) {
        return JSON.parseObject(title, TitleEO.class);
    }

    @Named("parseEducationToEO")
    default EducationEO parseEducationToEO(String education) {
        return JSON.parseObject(education, EducationEO.class);
    }


    @Named("parseDepartmentsToEO")
    default List<DepartmentEO> parseDepartmentsToEO(String departments) {
        return JSON.parseArray(departments, DepartmentEO.class);
    }

    @Named("parseCompaniesToEO")
    default List<CompanyEO> parseCompaniesToEO(String companies) {
        return JSON.parseArray(companies, CompanyEO.class);
    }


    @Named("parseUserToEO")
    default UserEO parseUserToEO(String user) {
        return JSON.parseObject(user, UserEO.class);
    }

    @Named("parsePositionsToEO")
    default List<PositionEO> parsePositionsToEO(String positions) {
        return JSON.parseArray(positions, PositionEO.class);
    }

    @Named("parseDirectLeaderToEO")
    default DirectLeaderEO parseDirectLeaderToEO(String directLeader) {
        return JSON.parseObject(directLeader, DirectLeaderEO.class);
    }

    @Named("parseGrandLeaderToEO")
    default GrandLeaderEO parseGrandLeaderToEO(String grandLeader) {
        return JSON.parseObject(grandLeader, GrandLeaderEO.class);
    }
}
















