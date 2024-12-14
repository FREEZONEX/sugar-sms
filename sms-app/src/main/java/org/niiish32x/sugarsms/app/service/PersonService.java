package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.external.PersonsResponse;
import org.niiish32x.sugarsms.common.result.Result;

import java.util.List;

/**
 * PersonService
 *
 * @author shenghao ni
 * @date 2024.12.08 18:40
 */
public interface PersonService {

    Result<List <PersonDTO>>  getPersonsFromSuposByPage(Integer currentPageSize);


    Result<PersonDTO>  getOnePersonByPersonCode(PersonCodesDTO personCodesDTO);

    Result <PersonsResponse>  getPersonsByPersonCodes(PersonCodesDTO personCodesDTOS);

    Result addPerson(String code);

    Result mockPerson();



}
