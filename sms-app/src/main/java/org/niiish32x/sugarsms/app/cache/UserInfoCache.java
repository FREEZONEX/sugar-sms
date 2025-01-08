package org.niiish32x.sugarsms.app.cache;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonsResponse;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.person.app.PersonService;
import org.niiish32x.sugarsms.common.enums.UserRoleEnum;
import org.niiish32x.sugarsms.person.app.external.PersonPageQueryRequest;
import org.niiish32x.sugarsms.user.app.UserService;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.user.app.external.UserPageQueryRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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


    public void load() {
        try {

            UserPageQueryRequest userPageQueryRequest = UserPageQueryRequest.builder()
                    .companyCode(CompanyEnum.DEFAULT.value)
                    .roleCode(UserRoleEnum.SUGAR.value)
                    .getAll(true)
                    .build();
            List<SuposUserDTO>  userDTOS = userService.getUsersFromSupos(userPageQueryRequest).getData();


            if (userDTOS == null || userDTOS.isEmpty()) {
                return;
            }

            List<String> personCodes = userDTOS.stream()
                    .map(SuposUserDTO::getPersonCode)
                    .collect(Collectors.toList());


            for (String code : personCodes) {
                PersonPageQueryRequest personPageQueryRequest = PersonPageQueryRequest.builder()
                        .codes(Lists.newArrayList(code))
                        .companyCode(CompanyEnum.DEFAULT.value)
                        .hasBoundUser(true)
                        .build();
                Result<List<PersonDTO>> res = personService.searchPeronFromSupos(personPageQueryRequest);

                Preconditions.checkArgument(res.isSuccess(),"person 缓存同步失败");

                 List<PersonDTO> personDTOS = res.getData();
                 for (PersonDTO personDTO : personDTOS) {
                     nameToPhone.put(personDTO.getName(), personDTO.getPhone());
                     nameToEmail.put(personDTO.getName(), personDTO.getEmail());
                 }

            }


//            Result<PersonsResponse> result = personService.getPersonsByPersonCodes(
//                    PersonCodesDTO.builder().personCodes(personCodes).build());
//
//            if (result.isSuccess()) {
//                PersonsResponse personsResponse = result.getData();
//                if (personsResponse != null && personsResponse.getList() != null) {
//                    for (PersonDTO personDTO : personsResponse.getList()) {
//                        nameToPhone.put(personDTO.getName(), personDTO.getPhone());
//                        nameToEmail.put(personDTO.getName(), personDTO.getEmail());
//                    }
//                }
//            }
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            // 或者进行其他异常处理逻辑
        }
    }
}
