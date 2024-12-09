package org.niiish32x.sugarsms.app.service;

import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;

import java.util.List;

/**
 * PersonService
 *
 * @author shenghao ni
 * @date 2024.12.08 18:40
 */
public interface PersonService {

    List <PersonDTO> getPersonsFromSuposByPage(Integer currentPageSize);


    PersonDTO getOnePersonByPersonCodes(PersonCodesDTO personCodesDTO);
}
