package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.niiish32x.sugarsms.app.dto.PageDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.service.SuposUserService;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
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
public class SuposUserServiceImpl implements SuposUserService {

    @Resource
    SuposRequestManager suposRequestManager;


    private final String USER_API_URI =  "/open-api/auth/v2/users";

    @Data
    class UserResponse implements Serializable {
        @JSONField(name = "list")
        private List<SuposUserDTO> userDTOS;

        @JSONField(name = "pagination")
        private PageDTO pageDTO;
    }

    @Override
    public List<SuposUserDTO> getUsersFromSupos(String company) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        // default_org_company
        queryMap.put("companyCode",company);
        HttpResponse response = suposRequestManager.suposApiGet(USER_API_URI, headerMap, queryMap);

        UserResponse userResponse = JSON.parseObject(response.body(), UserResponse.class);


        return userResponse.getUserDTOS();
    }

    @Override
    public List<SuposUserDTO> getUsersFromSupos(String companyCode, String roleCode) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("companyCode",companyCode);
        queryMap.put("roleCode",roleCode);
        HttpResponse response = suposRequestManager.suposApiGet(USER_API_URI, headerMap, queryMap);

        UserResponse userResponse = JSON.parseObject(response.body(), UserResponse.class);

        return userResponse.getUserDTOS();
    }
}
