package org.niiish32x.sugarsms.suposperson.app.assembler;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.niiish32x.sugarsms.api.person.dto.*;
import org.niiish32x.sugarsms.suposperson.domain.entity.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SuposPersonAssembler
 *
 * @author shenghao ni
 * @date 2025.01.09 11:19
 */

@Mapper
public interface SuposPersonAssembler {
    SuposPersonAssembler INSTANCE = Mappers.getMapper(SuposPersonAssembler.class);
}









