package org.niiish32x.sugarsms.user.app.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.api.user.dto.*;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.person.app.PersonService;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.user.app.UserService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.user.app.external.RolePageResponse;
import org.niiish32x.sugarsms.user.app.external.SuposUserAddRequest;
import org.niiish32x.sugarsms.user.app.external.UserPageQueryRequest;
import org.niiish32x.sugarsms.user.app.external.UsersResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    private static final String USER_API_PATH = ApiEnum.USER_PAGE_GET_API.value;


    @Resource
    PersonService personService;

    @Resource
    SuposRequestManager requestManager;



    @Override
    public Result addSuposUser(String username,String password) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.USER_PAGE_GET_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ? Result.success(response) : Result.error(JSON.toJSONString(response));
    }

    @Override
    public Result addSuposUser(String username, String password, List<String> roleNameList) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        SuposUserAddRequest request = new SuposUserAddRequest(username,password,roleNameList);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.USER_PAGE_GET_API.value, headerMap, queryMap, JSON.toJSONString(request));

        return response.isOk() ?  Result.success(response) : Result.error(JSON.toJSONString(response));
    }


    @Override
    public Result<List<SuposUserDTO>>  getUsersFromSupos(UserPageQueryRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap = request.buildQueryMap();
        if(!request.isGetAll()) {
            try {
                HttpResponse response = requestManager.suposApiGet(USER_API_PATH, headerMap, queryMap);
                UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

                if (response.isOk()) {
                    return Result.success(usersResponse.getList());
                } else {
                    log.error("Failed to fetch users from Supos: {}", response.body());
                    return Result.error(JSON.toJSONString(usersResponse));
                }
            } catch (Exception e) {
                log.error("Error occurred while fetching users from Supos", e);
                return Result.error("An error occurred while fetching users from Supos");
            }
        }else {

            UsersResponse res = new UsersResponse();
            int pageIndex = 1;

            while (true) {
                queryMap.put("pageSize","500");
                queryMap.put("pageIndex",String.valueOf(pageIndex));
                HttpResponse response = requestManager.suposApiGet(ApiEnum.USER_PAGE_GET_API.value, headerMap, queryMap);

                UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

                if( usersResponse.getList() == null  || usersResponse.getList().isEmpty()){
                    break;
                }
                res.getList().addAll(usersResponse.getList());
                pageIndex++;
            }


            return Result.success(res.getList());
        }
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
