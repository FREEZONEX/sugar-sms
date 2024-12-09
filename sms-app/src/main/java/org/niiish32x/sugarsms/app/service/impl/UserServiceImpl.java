package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.sun.org.apache.bcel.internal.generic.LUSHR;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.dto.MessageDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.SuposUserAddRequest;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SuposUserServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.08 13:34
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    PersonService personService;

    @Resource
    SuposRequestManager suposRequestManager;

    @Data
    class UsersResponse extends PageResponse implements Serializable {
        @JSONField(name = "list")
        private List<SuposUserDTO> list;
    }

    @Override
    public Result addSuposUser(String username,String password) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password);

        HttpResponse response = suposRequestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ?  Result.build(response.body(), ResultCodeEnum.SUCCESS) : Result.build(JSON.toJSONString(response.body()),ResultCodeEnum.FAIL);
    }

    @Override
    public Result addSuposUser(String username, String password, List<String> roleNameList) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password,roleNameList);

        HttpResponse response = suposRequestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ?  Result.build(response.body(), ResultCodeEnum.SUCCESS) : Result.build(JSON.toJSONString(response.body()),ResultCodeEnum.FAIL);
    }

    @Override
    public Result mockUser() {

        List<PersonDTO> persons = personService.getPersonsFromSuposByPage(1);

        List<String> roleNameList = new ArrayList<>();
        roleNameList.add("sugarsms");

        for (PersonDTO personDTO : persons) {
            String password = SuposUserMocker.generatePassword();
            Result res = addSuposUser(personDTO.getName(), password, roleNameList);

            if(Objects.equals(res.getCode(), ResultCodeEnum.CODE_ERROR.getCode())) {
                return res;
            }
        }

        return getUsersFromSupos("default_org_company");
    }

    @Override
    public Result getUsersFromSupos(String company) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        // default_org_company
        queryMap.put("companyCode",company);
        HttpResponse response = suposRequestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);

        UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

        return response.isOk() ? Result.build(usersResponse,ResultCodeEnum.SUCCESS) : Result.build(JSON.toJSONString(response),ResultCodeEnum.FAIL);
    }

    @Override
    public Result<List<SuposUserDTO>>  getUsersFromSupos(String companyCode, String roleCode) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("companyCode",companyCode);
        queryMap.put("roleCode",roleCode);
        HttpResponse response = suposRequestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);

        UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

        return Result.build(usersResponse.getList(),ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result<SuposUserDTO> role(String username, String role) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        return null;
    }


    @Data
    class UserMessagesResponse extends PageResponse implements Serializable {
        @JSONField(name = "list")
        private List<MessageDTO> list;
    }

    @Override
    public Result getMessageReceived(String username) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("startTime","2021-01-26T16:02:15.666+0800");
        queryMap.put("endTime",getTime()+"+0800");

        HttpResponse response = suposRequestManager.suposApiGet("/open-api/p/notification/v2alpha1/users/" + username + "/messgae", headerMap, queryMap);
        UserMessagesResponse userMessagesResponse = JSON.parseObject(response.body(), UserMessagesResponse.class);

        return Result.build(userMessagesResponse.getList(),ResultCodeEnum.SUCCESS);
    }

    private String getTime() {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");
        String formattedTime = now.format(formatter);
        return formattedTime;
    }
}
