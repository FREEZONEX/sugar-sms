package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.app.dto.PageDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.SuposPersonAddRequest;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
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


    @Data
    class PersonsResponse extends PageResponse {
        @JSONField(name = "list")
        private List<PersonDTO> list;
        @JSONField(name = "pagination")
        private PageDTO pageDTO;
    }

    @Override
    public List<PersonDTO> getPersonsFromSuposByPage(Integer currentPageSize) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("current", String.valueOf(currentPageSize));
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PESRON_GET_API.value, headerMap, queryMap);

        PersonsResponse personsResponse = JSON.parseObject(response.body(),PersonsResponse.class);

        return personsResponse.getList();
    }

    @Override
    public PersonDTO getOnePersonByPersonCodes(PersonCodesDTO personCodesDTO) {

        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        String join = String.join(",", personCodesDTO.getPersonCodes());
        queryMap.put("personCodes", join);
        queryMap.put("current","1");
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PESRON_GET_API.value, headerMap, queryMap);

        PersonsResponse dto = JSON.parseObject(response.body(), PersonsResponse.class);

        return dto.getList().get(0);
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

        HttpResponse response = requestManager.suposApiPost(ApiEnum.PESRON_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        log.info(response.body());

        return response.isOk() ? Result.build(response.body(), ResultCodeEnum.SUCCESS) : Result.build(response.body(),ResultCodeEnum.FAIL);
    }

    @Override
    public Result mockPerson() {
        for (int i = 0 ; i < 10 ; i++) {
            String username = SuposUserMocker.generateUsername();
            Result res = addPerson(username);
            if(Objects.equals(res.getCode(), ResultCodeEnum.CODE_ERROR.getCode())) {
                return res;
            }
        }

        return Result.build("mock完成",ResultCodeEnum.SUCCESS);
    }


}
