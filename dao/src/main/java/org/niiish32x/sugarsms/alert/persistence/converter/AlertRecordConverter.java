package org.niiish32x.sugarsms.alert.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;

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

    @Mapping(target = "smsSendStatus", expression = "java(alertRecordEO.getSmsSendStatus() ? 1 : 0)")
    @Mapping(target = "emailSendStatus" , expression = "java(alertRecordEO.getEmailSendStatus() ? 1 : 0)")
    @Mapping(target = "status" , expression = "java(alertRecordEO.getStatus() ? 1 : 0)")
    AlertRecordDO toDO(AlertRecordEO alertRecordEO);

    @Mapping(target = "smsSendStatus", expression = "java(alertRecordDO.getSmsSendStatus() == 1 ? true : false)")
    @Mapping(target = "emailSendStatus", expression = "java(alertRecordDO.getEmailSendStatus() == 1 ? true : false)")
    @Mapping(target = "status", expression = "java(alertRecordDO.getStatus() == 1 ? true : false)")
    AlertRecordEO toEO(AlertRecordDO alertRecordDO);
}
