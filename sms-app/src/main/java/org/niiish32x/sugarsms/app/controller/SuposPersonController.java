package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * PersonController
 *
 * @author shenghao ni
 * @date 2024.12.08 19:03
 */
@RestController
public class SuposPersonController {
    @Resource
    PersonService personService;

    @RequestMapping("/persons/all")
    public List<PersonDTO> getAllPerson(@RequestParam Integer currentPageSize) {
       return  personService.getPersonsFromSuposByPage(currentPageSize);
    }

    @RequestMapping("/persons/personCodes")
    public PersonDTO getOnePersonByPersonCode(@RequestBody PersonCodesDTO personCodesDTO){
        return personService.getOnePersonByPersonCodes(personCodesDTO);
    }

    @RequestMapping("/persons/mock")
    public Result mockPerson(){
        return personService.mockPerson();
    }
}
