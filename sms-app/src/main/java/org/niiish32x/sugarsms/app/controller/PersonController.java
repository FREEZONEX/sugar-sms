package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * PersonController
 *
 * @author shenghao ni
 * @date 2024.12.08 19:03
 */
@RestController
public class PersonController {
    @Resource
    PersonService personService;

    @RequestMapping("/persons/all")
    public List<PersonDTO> getAllPeron(@RequestParam Integer currentPageSize) {
       return  personService.getPersonsFromSuposByPage(currentPageSize);
    }

}
