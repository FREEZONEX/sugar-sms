package org.niiish32x.sugarsms.alarm.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;

/**
 * AlarmAssembler
 *
 * @author shenghao ni
 * @date 2024.12.29 17:48
 */


@Mapper
public interface AlarmAssembler {
    AlarmAssembler INSTANCE = Mappers.getMapper(AlarmAssembler.class);
    AlarmDTO alarmEO2DTO(AlarmEO alarmEO);


    AlarmEO alarmDTO2EO(AlarmDTO alarmDTO);
}
