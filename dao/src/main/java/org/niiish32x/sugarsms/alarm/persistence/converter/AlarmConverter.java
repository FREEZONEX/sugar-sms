package org.niiish32x.sugarsms.alarm.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alarm.AlarmDO;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;

import java.util.Map;

/**
 * AlarmConverter
 *
 * @author shenghao ni
 * @date 2024.12.27 14:32
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface AlarmConverter {
    AlarmConverter INSTANCE = Mappers.getMapper(AlarmConverter.class);

    @Mapping(target = "deleted", expression = "java(alarmEO.isDeleted() ? 1 : 0)")
    AlarmDO toDO(AlarmEO alarmEO);

    @Mapping(target = "deleted", expression = "java(alarmDO.getDeleted() == 1 ? true : false)")
    AlarmEO toEO(AlarmDO alarmDO);
}
