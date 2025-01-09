package org.niiish32x.sugarsms.suposperson.app;

import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonDTO;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonUpdateRequest;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.suposperson.app.external.PersonPageQueryRequest;
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
    SuposPersonService suposPersonService;

    @RequestMapping("/persons/personCodes")
    public Result<SuposPersonDTO>  getOnePersonByPersonCode(@RequestBody PersonCodesDTO personCodesDTO){
        return suposPersonService.getOnePersonByPersonCode(personCodesDTO);
    }

    @RequestMapping("/persons/add")
    public Result  addPersonByPersonCode(@RequestParam String code) {
       return suposPersonService.addPerson(code);
    }


    @RequestMapping("/persons/update")
    public Result  addPersonByPersonCode(@RequestBody SuposPersonUpdateRequest req) {
        return suposPersonService.updatePerson(req);
    }


    @RequestMapping("/persons/mock")
    public Result mockPerson(){
        return suposPersonService.mockPerson();
    }


    @RequestMapping("/persons")
    public Result  getAllPerson() {
       return  suposPersonService.getAllPerson();

    }


    @RequestMapping("/persons/test")
    public Result test(@RequestBody PersonPageQueryRequest request){
        return suposPersonService.searchPeronFromSupos(request);
    }
}
