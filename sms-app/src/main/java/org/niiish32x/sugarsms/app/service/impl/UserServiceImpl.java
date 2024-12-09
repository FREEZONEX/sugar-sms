package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.SuposUserAddRequest;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.app.tools.SuposUserMocker;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SuposUserServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.08 13:34
 */
@Service
public class UserServiceImpl implements UserService {

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
    public Result mockUser() {
        for(int i = 0  ; i < 10 ; i++) {
            String username = SuposUserMocker.generateUsername();
            String password = SuposUserMocker.generatePassword();
            addSuposUser(username,password);
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
    public List<SuposUserDTO> getUsersFromSupos(String companyCode, String roleCode) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("companyCode",companyCode);
        queryMap.put("roleCode",roleCode);
        HttpResponse response = suposRequestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);

        UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

        return usersResponse.getList();
    }

    @Override
    public Result<SuposUserDTO> role(String username, String role) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        return null;
    }
}
