package org.niiish32x.sugarsms.alert.app.assmbler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertRecordDTO;

import java.util.Map;

/**
 * AlertRecordAssmbler
 *
 * @author shenghao ni
 * @date 2025.01.21 14:04
 */
@Mapper(imports = {JSON.class, TypeReference.class, Map.class})
public interface AlertRecordAssembler {
    AlertRecordAssembler INSTANCE = Mappers.getMapper(AlertRecordAssembler.class);


    AlertRecordDTO toDTO(AlertRecordEO alertRecordEO);
}
