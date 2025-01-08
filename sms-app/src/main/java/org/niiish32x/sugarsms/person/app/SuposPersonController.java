package org.niiish32x.sugarsms.person.app;

import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonUpdateRequest;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.person.app.external.PersonPageQueryRequest;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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

    @RequestMapping("/persons/personCodes")
    public Result<PersonDTO>  getOnePersonByPersonCode(@RequestBody PersonCodesDTO personCodesDTO){
        return personService.getOnePersonByPersonCode(personCodesDTO);
    }

    @RequestMapping("/persons/add")
    public Result  addPersonByPersonCode(@RequestParam String code) {
       return personService.addPerson(code);
    }


    @RequestMapping("/persons/update")
    public Result  addPersonByPersonCode(@RequestBody SuposPersonUpdateRequest req) {
        return personService.updatePerson(req);
    }


    @RequestMapping("/persons/mock")
    public Result mockPerson(){
        return personService.mockPerson();
    }

    @RequestMapping("/persons/test")
    public Result test(@RequestBody PersonPageQueryRequest request){
        return personService.searchPeronFromSupos(request);
    }
}
