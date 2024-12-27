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
import org.niiish32x.sugarsms.app.external.*;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.PageResponse;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
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


    private static final String COMPANY_CODE_KEY = "companyCode";
    private static final String USER_API_PATH = ApiEnum.USER_API.value;




    @Resource
    PersonService personService;

    @Resource
    SuposRequestManager requestManager;



    @Override
    public Result addSuposUser(String username,String password) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ? Result.success(response) : Result.error(JSON.toJSONString(response));
    }

    @Override
    public Result addSuposUser(String username, String password, List<String> roleNameList) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password,roleNameList);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.USER_API.value, headerMap, queryMap, JSON.toJSONString(request));

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
        if (company == null || company.trim().isEmpty()) {
            return Result.error("Company code cannot be null or empty");
        }

        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put(COMPANY_CODE_KEY, company);

        try {
            HttpResponse response = requestManager.suposApiGet(USER_API_PATH, headerMap, queryMap);
            UsersPageResponse usersPageResponse = JSON.parseObject(response.body(), UsersPageResponse.class);

            if (response.isOk()) {
                return Result.success(usersPageResponse);
            } else {
                log.error("Failed to fetch users from Supos: {}", response.body());
                return Result.error(JSON.toJSONString(usersPageResponse));
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching users from Supos", e);
            return Result.error("An error occurred while fetching users from Supos");
        }
    }

    @Override
    public Result<List<SuposUserDTO>> getUsersFromSupos(String companyCode, String roleCode) {
        // 输入验证
        if (companyCode == null || companyCode.isEmpty() || roleCode == null || roleCode.isEmpty()) {
            return Result.error("Invalid input parameters");
        }

        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        List<SuposUserDTO> userList = Collections.synchronizedList(new ArrayList<>());
        int pageSize = 500;
        int maxPageIndex = 100; // 设置最大页数限制
        int pageIndex = 1;

        while (pageIndex <= maxPageIndex) {
            queryMap.put("companyCode", companyCode);
            queryMap.put("roleCode", roleCode);
            queryMap.put("pageSize", String.valueOf(pageSize));
            queryMap.put("pageIndex", String.valueOf(pageIndex));

            try {
                HttpResponse response = requestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);
                UsersPageResponse usersPageResponse = JSON.parseObject(response.body(), UsersPageResponse.class);

                if (usersPageResponse.getList() == null || usersPageResponse.getList().isEmpty()) {
                    break;
                }
                userList.addAll(usersPageResponse.getList());
                pageIndex++;
            } catch (Exception e) {
                // 记录日志并返回错误信息
                log.error("Error fetching users from Supos: " + e.getMessage(), e);
                return Result.error("Failed to fetch users: " + e.getMessage());
            }
        }

        if (pageIndex > maxPageIndex) {
            log.warn("Reached maximum page index limit without exhausting results.");
        }

        return Result.success(userList);
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

        HttpResponse response = requestManager.suposApiGet("/open-api/p/notification/v2alpha1/users/" + username + "/messages", headerMap, queryMap);
        UserMessagesResponse userMessagesResponse = JSON.parseObject(response.body(), UserMessagesResponse.class);

        System.out.println(JSON.toJSONString(response));

        return Result.success(userMessagesResponse.getList());

    }


    @Override
    public Result<List<RoleSpecDTO>> getRoleListFromSupos(String companyCode) {

        // 验证输入参数
        if (companyCode == null || companyCode.trim().isEmpty()) {
            log.warn("Invalid companyCode: {}", companyCode);
            return Result.error("Invalid company code");
        }

        try {
            Map<String, String> headerMap = new HashMap<>();
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("companyCode", companyCode);

            HttpResponse response = requestManager.suposApiGet(ApiEnum.USER_ROLE_GET_API.value, headerMap, queryMap);

            // 检查响应是否为空
            if (response == null || response.body() == null || response.body().trim().isEmpty()) {
                log.warn("Empty response body for companyCode: {}", companyCode);
                return Result.error("Empty response body");
            }

            RolePageResponse rolePageResponse = JSON.parseObject(response.body(), RolePageResponse.class);

            // 检查 rolePageResponse 是否为空
            if (rolePageResponse == null) {
                log.warn("Failed to parse response body for companyCode: {}", companyCode);
                return Result.error("Failed to parse response body");
            }

            List<RoleSpecDTO> roleList = rolePageResponse.getList();
            if (roleList == null) {
                roleList = Collections.emptyList();
            }

            return response.isOk() ? Result.success(roleList) : Result.error("API call failed");

        } catch (Exception e) {
            log.error("Error occurred while fetching role list for companyCode: {}", companyCode, e);
            return Result.error("Internal server error");
        }
    }

    private String getTime() {
        OffsetDateTime now = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX");
        String formattedTime = now.format(formatter);
        return formattedTime;
    }
}
