package org.niiish32x.sugarsms.alarm.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alarm.AlarmDO;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.entity.AttributeLabelEO;
import org.niiish32x.sugarsms.alarm.domain.entity.InstanceLabelEO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * AlarmConverter
 *
 * @author shenghao ni
 * @date 2024.12.27 14:32
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface AlarmConverter {
    AlarmConverter INSTANCE = Mappers.getMapper(AlarmConverter.class);

    @Mapping(target = "attributeLabels", expression = "java(JSON.toJSONString(alarmEO.getAttributeLabels()))")
    @Mapping(target = "instanceLabels", expression = "java(JSON.toJSONString(alarmEO.getInstanceLabels()))")
    @Mapping(target = "deleted", expression = "java(alarmEO.isDeleted() ? 1 : 0)")
    AlarmDO toDO(AlarmEO alarmEO);


    @Mapping(target = "instanceLabels", source = "instanceLabels", qualifiedByName = "parseInstanceLabelsToEO")
    @Mapping(target = "attributeLabels", source = "attributeLabels", qualifiedByName = "parseAttributeLabelsToEO")
    @Mapping(target = "deleted", expression = "java(alarmDO.getDeleted() == 1 ? true : false)")
    AlarmEO toEO(AlarmDO alarmDO);

    @Named("parseInstanceLabelsToEO")
    default List<InstanceLabelEO> parseInstanceLabelsToEO(String instanceLabels) {
        return Optional.ofNullable(instanceLabels)
                .map(JSON::parseObject)
                .map(jsonObject -> jsonObject.toJavaObject(new TypeReference<List<InstanceLabelEO>>() {}))
                .orElse(Lists.newArrayList());
    }


    @Named("parseAttributeLabelsToEO")
    default List<AttributeLabelEO> parseAttributeLabelsToEO(String attributeLabels) {
        return Optional.ofNullable(attributeLabels)
                .map(JSON::parseObject)
                .map(jsonObject -> jsonObject.toJavaObject(new TypeReference<List<AttributeLabelEO>>() {}))
                .orElse(Lists.newArrayList());
    }
}
