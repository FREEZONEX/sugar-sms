package org.niiish32x.sugarsms.alert.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.user.domain.entity.UserEO;

import java.util.Map;

/**
 * AlertRecordfConverter
 *
 * @author shenghao ni
 * @date 2024.12.16 18:32
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface AlertRecordConverter {
    AlertRecordConverter INSTANCE = Mappers.getMapper(AlertRecordConverter.class);

    @Mapping(target = "type",expression = "java(alertRecordEO.getType().name())")
    @Mapping(target = "status" , expression = "java(alertRecordEO.getStatus() ? 1 : 0)")
    @Mapping(target = "alarm", expression = "java(JSON.toJSONString(alertRecordEO.getAlarm()))")
    @Mapping(target = "expire", expression = "java(alertRecordEO.getExpire() ? 1 : 0)")
    @Mapping(target = "user" , expression = "java(JSON.toJSONString(alertRecordEO.getUser()))")
    AlertRecordDO toDO(AlertRecordEO alertRecordEO);

    @Mapping(target = "type", source = "type",qualifiedByName = "parseMessageTypeToEO")
    @Mapping(target = "status", expression = "java(alertRecordDO.getStatus() == 1 ? true : false)")
    @Mapping(target = "alarm", source = "alarm" , qualifiedByName = "parseAlarmToEO")
    @Mapping(target = "expire", expression = "java(alertRecordDO.getExpire() == 1 ? true : false)")
    @Mapping(target = "user", source = "user" , qualifiedByName = "parseUserToEO")
    AlertRecordEO toEO(AlertRecordDO alertRecordDO);


    @Named("parseUserToEO")
    default UserEO parseUserToEO(String user) {
        return JSON.parseObject(user, UserEO.class);
    }

    @Named("parseMessageTypeToEO")
    default MessageType parseMessageTypeToEO (String type) {
        if (StringUtils.equals(type , "SMS")) {
            return MessageType.SMS;
        }

        if (StringUtils.equals(type , "EMAIL")){
            return MessageType.EMAIL;
        }

        return null;
    }

    @Named("parseAlarmToEO")
    default AlarmEO parseAlarmToEO(String alarm) {
        return JSON.parseObject(alarm, AlarmEO.class);
    }

}
