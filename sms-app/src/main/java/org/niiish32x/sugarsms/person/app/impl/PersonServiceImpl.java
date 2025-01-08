package org.niiish32x.sugarsms.person.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.api.person.dto.PersonsResponse;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonAddRequest;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonUpdateRequest;
import org.niiish32x.sugarsms.person.app.PersonService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.person.app.external.PersonPageQueryRequest;
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
    public Result<List<PersonDTO>> personPageSearchFromSupos(PersonPageQueryRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        try {
            if (!request.isGetAll()) {
                queryMap = request.buildQueryMap();
                HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API_V25.value, headerMap, queryMap);
                PersonsResponse personsResponse = JSON.parseObject(response.body(), PersonsResponse.class);
                if (response.isOk()) {
                    return Result.success(personsResponse.getList());
                } else {
                    log.error("Failed to fetch persons from Supos: {}", response.body());
                    return Result.error(JSON.toJSONString(personsResponse));
                }
            }else {
                PersonsResponse res = new PersonsResponse();
                res.setList(new ArrayList<>());
                int pageNo = 1;

                while (true) {
                    queryMap = request.buildQueryMap();
                    queryMap.put("pageNo",String.valueOf(pageNo));
                    System.out.println(JSON.toJSONString(queryMap));
                    HttpResponse response = requestManager.suposApiGet(ApiEnum.PERSON_GET_API_V25.value, headerMap, queryMap);
                    PersonsResponse personsResponse = JSON.parseObject(response.body(), PersonsResponse.class);

                    if (!response.isOk()) {
                        log.error("Failed to fetch persons from Supos: {}", response.body());
                        return Result.error(JSON.toJSONString(personsResponse));
                    }

                    if (personsResponse.getList() == null || personsResponse.getList().isEmpty()) {
                        break;
                    }

                    res.getList().addAll(personsResponse.getList());
                    pageNo++;
                }

                return Result.success(res.getList());
            }

        }catch (Exception e) {
            log.error("Error occurred while fetching persons from Supos", e);
            return Result.error("An error occurred while fetching persons from Supos");
        }
    }

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
}
