package org.niiish32x.sugarsms.alert.persistence.converter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alert.AlertDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;

import java.util.Map;

/**
 * AlertConverter
 *
 * @author shenghao ni
 * @date 2025.01.17 10:37
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface AlertConverter {
    AlertConverter INSTANCE = Mappers.getMapper(AlertConverter.class);

    @Mapping(target = "finishGenerateAlertRecord", expression = "java(alertEO.isFinishGenerateAlertRecord()?1:0)")
    AlertDO toDO(AlertEO alertEO);

    @Mapping(target = "finishGenerateAlertRecord", expression = "java(alertDO.getFinishGenerateAlertRecord()==1?true:false)")
    AlertEO toEO(AlertDO alertDO);
}
