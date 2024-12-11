package org.niiish32x.sugarsms.app.cache;

import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.PersonsResponse;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UserPhoneCache
 *
 * @author shenghao ni
 * @date 2024.12.10 16:27
 */

@Component
public class UserPhoneCache implements InitializingBean {

    @Resource
    UserService userService;

    @Resource
    PersonService personService;

    public static Cache<String,String> cache = CacheBuilder.newBuilder()
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
                cache.put(personDTO.getName(),personDTO.getPhone());
            }
        }
    }
}
