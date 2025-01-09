package org.niiish32x.sugarsms.suposperson.app;


import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonUpdateRequest;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.suposperson.app.external.PersonPageQueryRequest;
import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;

import java.util.List;

/**
 * PersonService
 *
 * @author shenghao ni
 * @date 2024.12.08 18:40
 */
public interface SuposPersonService {


    Result<List<SuposPersonEO>> getAllPerson();

    /**
     * 分页查询 person 从supos
     * @param request
     * @return
     */
    Result<List<PersonDTO>> searchPeronFromSupos(PersonPageQueryRequest request);


    Result<PersonDTO>  getOnePersonByPersonCode(PersonCodesDTO personCodesDTO);


    Result addPerson(String code);

    Result updatePerson(SuposPersonUpdateRequest request);

    Result mockPerson();

}
