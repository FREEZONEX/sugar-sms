package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.dto.MessageDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.SuposUserAddRequest;
import org.niiish32x.sugarsms.app.external.UsersResponse;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.PageResponse;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.result.ResultCode;
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



    @Override
    public Result addSuposUser(String username,String password) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password);

        HttpResponse response = suposRequestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ? Result.success(response) : Result.error(JSON.toJSONString(response));
    }

    @Override
    public Result addSuposUser(String username, String password, List<String> roleNameList) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password,roleNameList);

        HttpResponse response = suposRequestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ?  Result.success(response) : Result.error(JSON.toJSONString(response));
    }

    @Override
    public Result mockUser() {
        for (int i = 1 ;  i <= 10 ;  i++){
            List<PersonDTO> persons = personService.getPersonsFromSuposByPage(i).getData();

            List<String> roleNameList = new ArrayList<>();
            roleNameList.add("sugarsms");

            for (PersonDTO personDTO : persons) {
                String password = SuposUserMocker.generatePassword();
                Result res = addSuposUser(personDTO.getName(), password, roleNameList);

                if(!Objects.equals(res.getCode(), 200)) {
                    return res;
                }
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

        return response.isOk() ? Result.success(usersResponse) : Result.error(JSON.toJSONString(usersResponse));
    }

    @Override
    public Result<List<SuposUserDTO>>  getUsersFromSupos(String companyCode, String roleCode) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        UsersResponse res = new UsersResponse();
        int pageIndex = 1;
        while (true) {
            queryMap.put("companyCode",companyCode);
            queryMap.put("roleCode",roleCode);
            queryMap.put("pageSize","500");
            queryMap.put("pageIndex",String.valueOf(pageIndex));
            HttpResponse response = suposRequestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);

            UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

            if( usersResponse.getList() == null  || usersResponse.getList().isEmpty()){
                break;
            }
            res.getList().addAll(usersResponse.getList());
            pageIndex++;
        }


        return Result.success(res.getList());
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
        queryMap.put("noticeProtocol","stationLetter");

        HttpResponse response = suposRequestManager.suposApiGet("/open-api/p/notification/v2alpha1/users/" + username + "/messages", headerMap, queryMap);
        UserMessagesResponse userMessagesResponse = JSON.parseObject(response.body(), UserMessagesResponse.class);

        System.out.println(JSON.toJSONString(response));

        return Result.success(userMessagesResponse.getList());

    }

    private String getTime() {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");
        String formattedTime = now.format(formatter);
        return formattedTime;
    }
}
