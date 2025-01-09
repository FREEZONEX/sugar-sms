package org.niiish32x.sugarsms.suposperson.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import org.niiish32x.sugarsms.suposperson.SuposPersonDO;
import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;


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

    @Mapping(target = "gender",expression = "java(JSON.toJSONString(personEO.getGender()))")
    @Mapping(target = "status",expression = "java(JSON.toJSONString(personEO.getStatus()))")
    @Mapping(target = "mainPosition", expression = "java(JSON.toJSONString(personEO.getMainPosition()))")
    @Mapping(target = "code", expression = "java(JSON.toJSONString(personEO.getCode()))")
    @Mapping(target = "title", expression = "java(JSON.toJSONString(personEO.getTitle()))")
    @Mapping(target = "education", expression = "java(JSON.toJSONString(personEO.getEducation()))")
    SuposPersonDO toDO (SuposPersonEO suposPersonEO) ;


    @Mapping(target = "gender",expression = "java(JSON.parseObject(suposPersonDO.getGender(), new TypeReference<Map<String, Object>>(){}))")
    @Mapping(target = "status",expression = "java(JSON.parseObject(suposPersonDO.getStatus(), new TypeReference<Map<String, Object>>(){}))")
    @Mapping(target = "mainPosition", expression = "java(JSON.parseObject(suposPersonDO.getMainPosition(), new TypeReference<Map<String, Object>>(){}))")
    @Mapping(target = "code", expression = "java(JSON.parseObject(suposPersonDO.getCode(), new TypeReference<Map<String, Object>>(){}))")
    @Mapping(target = "title", expression = "java(JSON.parseObject(suposPersonDO.getTitle(), new TypeReference<Map<String, Object>>(){}))")
    @Mapping(target = "education", expression = "java(JSON.parseObject(suposPersonDO.getEducation(), new TypeReference<Map<String, Object>>(){}))")
    SuposPersonEO toEO (SuposPersonDO suposPersonDO) ;
}
















