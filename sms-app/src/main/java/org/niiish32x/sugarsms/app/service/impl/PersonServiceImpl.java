package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.PersonsResponse;
import org.niiish32x.sugarsms.app.external.SuposPersonAddRequest;
import org.niiish32x.sugarsms.app.external.SuposPersonUpdateRequest;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * PersonServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.08 18:41
 */

@Service
@Slf4j
public class PersonServiceImpl implements PersonService {

    @Resource
    SuposRequestManager requestManager;


    @Override
    public Result <List<PersonDTO>>  getPersonsFromSuposByPage(Integer currentPageSize) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("current", String.valueOf(currentPageSize));
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API.value, headerMap, queryMap);

        PersonsResponse personsResponse = JSON.parseObject(response.body(),PersonsResponse.class);

        return Result.success(personsResponse.getList()) ;
    }

    @Override
    public Result<List<PersonDTO>> getTotalPersons() {

        List<PersonDTO> res = new ArrayList<>();

        for (int i = 1 ;  ; i++) {
            Result<List<PersonDTO>> personsRes = getPersonsFromSuposByPage(i);

            if (personsRes.getData().isEmpty()) {
                return Result.success(res);
            }

            res.addAll(personsRes.getData());
        }
    }

    @Override
    public Result<PersonDTO>  getOnePersonByPersonCode(PersonCodesDTO personCodesDTO) {

        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        String join = String.join(",", personCodesDTO.getPersonCodes());
        queryMap.put("personCodes", join);
        queryMap.put("current","1");
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API.value, headerMap, queryMap);

        PersonsResponse dto = JSON.parseObject(response.body(), PersonsResponse.class);

        return Result.success(dto.getList().get(0)) ;
    }

    @Override
    public Result <PersonsResponse> getPersonsByPersonCodes(PersonCodesDTO personCodesDTOS) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        String join = String.join(",", personCodesDTOS.getPersonCodes());
        queryMap.put("personCodes", join);
        queryMap.put("current","1");
        queryMap.put("pageSize","500");

        HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API.value, headerMap, queryMap);

        PersonsResponse dto = JSON.parseObject(response.body(), PersonsResponse.class);
        return  response.isOk() ? Result.success(dto) : Result.error("获取person信息异常");
    }

    @Override
    public Result addPerson(String code) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposPersonAddRequest request = new SuposPersonAddRequest(code);

        List<SuposPersonAddRequest> list = new ArrayList<>();
        list.add(request);

        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("addPersons" , list);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.PERSON_BATCH_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        return response.isOk() ? Result.success(JSON.toJSONString(response.body())) : Result.error(JSON.toJSONString(response.body()));
    }

    @Override
    public Result updatePerson(SuposPersonUpdateRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();


        Map<String,Object> jsonMap = new HashMap<>();

        jsonMap.put("updatePersons" , Arrays.asList(request));

        HttpResponse response = requestManager.suposApiPost(ApiEnum.PERSON_BATCH_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        log.info("update person : {}" ,JSON.toJSONString(response.body()));

        return response.isOk() ? Result.success(request) : Result.error("修改失败: " + JSON.toJSONString(response));
    }

    @Override
    public Result mockPerson() {
        for (int i = 0 ; i < 10 ; i++) {
            String username = SuposUserMocker.generateUsername();
            Result res = addPerson(username);
            if(!Objects.equals(res.getCode(),200)) {
                return res;
            }
        }

        return Result.success("mock完成");
    }

    @Override
    public Result test() {
        Result<List<PersonDTO>> result = getTotalPersons();

        if(!result.isSuccess()) {
            return Result.error("查找人员失败");
        }

        List<PersonDTO> personDTOS = result.getData();

        if (personDTOS.isEmpty()) {
            Result.error("persons 为空");
        }

        for (PersonDTO personDTO : personDTOS) {

            if(StringUtils.equals(personDTO.getPhone(),"919747934655")){
                log.info("person: {} {}",personDTO.getName(),personDTO.getPhone());
                Result updatedRes = updatePerson(SuposPersonUpdateRequest.builder()
                        .phone("919747934600")
                        .name(personDTO.getName())
                        .mainPositionCode(personDTO.getMainPosition().getCode())
                        .status(personDTO.getStatus().getCode())
                        .gender(personDTO.getGender().getCode())
                        .code(personDTO.getCode())
                        .build());

                if (!updatedRes.isSuccess()) {
                    Result.error(JSON.toJSONString(personDTO));
                }
            }
        }

        return Result.success("全部更新成功");
    }


}
