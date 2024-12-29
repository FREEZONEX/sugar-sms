package org.niiish32x.sugarsms.alarm.app.assembler;

import com.alibaba.fastjson2.JSON;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.entity.AttributeLabelEO;
import org.niiish32x.sugarsms.alarm.domain.entity.InstanceLabelEO;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alarm.dto.AttributeLabelDTO;
import org.niiish32x.sugarsms.api.alarm.dto.InstanceLabelDTO;


import java.util.List;

/**
 * AlarmAssembler
 *
 * @author shenghao ni
 * @date 2024.12.29 17:48
 */


@Mapper
public interface AlarmAssembler {
    AlarmAssembler INSTANCE = Mappers.getMapper(AlarmAssembler.class);

    @Mapping(target = "instanceLabels", source = "instanceLabels", qualifiedByName = "parseInstanceLabelsToDTO")
    @Mapping(target = "attributeLabels", source = "attributeLabels", qualifiedByName = "parseAttributeLabelsToDTO")
    AlarmDTO alarmEO2DTO(AlarmEO alarmEO);


    AlarmEO alarmDTO2EO(AlarmDTO alarmDTO);


    @Named("parseInstanceLabelsToDTO")
    default List<InstanceLabelDTO> parseInstanceLabelsToDTO(List<InstanceLabelEO> instanceLabels) {
        return JSON.parseArray(JSON.toJSONString(instanceLabels), InstanceLabelDTO.class);
    }


    @Named("parseAttributeLabelsToDTO")
    default List<AttributeLabelDTO> parseAttributeLabelsToDTO(List<AttributeLabelEO> attributeLabels) {
        return JSON.parseArray(JSON.toJSONString(attributeLabels), AttributeLabelDTO.class);
    }

}
