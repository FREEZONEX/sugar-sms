package org.niiish32x.sugarsms.app.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.PersonsResponse;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UserPhoneCache
 *
 * @author shenghao ni
 * @date 2024.12.10 16:27
 */

@Component
public class UserInfoCache implements InitializingBean {

    @Resource
    UserService userService;

    @Resource
    PersonService personService;

    public static Cache<String,String> nameToEmail = CacheBuilder.newBuilder()
            .expireAfterAccess(300, TimeUnit.SECONDS)
            .build();

    public static Cache<String,String> nameToPhone = CacheBuilder.newBuilder()
            .expireAfterAccess(300, TimeUnit.SECONDS)
            .build();

    /**
     * 缓存预热
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        load();
    }

    public  void load() {
        List<SuposUserDTO> userDTOS = userService.getUsersFromSupos("default_org_company", "sugarsms").getData();

        List<String> personCodes = new ArrayList<>();

        for (SuposUserDTO userDTO : userDTOS) {
            personCodes.add(userDTO.getPersonCode());
        }

        Result<PersonsResponse> result = personService.getPersonsByPersonCodes(PersonCodesDTO.builder()
                .personCodes(personCodes)
                .build());

        if (result.isOk()) {
            PersonsResponse personsResponse = result.getData();
            List<PersonDTO> personDTOS = personsResponse.getList();

            for (PersonDTO personDTO : personDTOS) {
                nameToPhone.put(personDTO.getName(),personDTO.getPhone());
                nameToEmail.put(personDTO.getName(),personDTO.getEmail());
            }
        }
    }
}
