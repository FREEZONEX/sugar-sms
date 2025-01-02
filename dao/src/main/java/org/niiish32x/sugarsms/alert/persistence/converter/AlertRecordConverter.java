package org.niiish32x.sugarsms.alert.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;

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
    AlertRecordDO toDO(AlertRecordEO alertRecordEO);

    @Mapping(target = "type",expression = "java(parseMessageTypeTDO(alertRecordDO))")
    @Mapping(target = "status", expression = "java(alertRecordDO.getStatus() == 1 ? true : false)")
    AlertRecordEO toEO(AlertRecordDO alertRecordDO);

    default MessageType parseMessageTypeTDO (AlertRecordDO alertRecordDO) {
        if (StringUtils.equals(alertRecordDO.getType(), "SMS")) {
            return MessageType.SMS;
        }

        if (StringUtils.equals(alertRecordDO.getType(),"EMAIL")){
            return MessageType.EMAIL;
        }

        return null;
    }

}
