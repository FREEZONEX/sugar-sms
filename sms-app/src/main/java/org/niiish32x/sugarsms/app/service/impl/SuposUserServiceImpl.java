package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.niiish32x.sugarsms.app.dto.PageDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.service.SuposUserService;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;
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

    @Data
    class UsersResponse extends PageResponse implements Serializable {
        @JSONField(name = "list")
        private List<SuposUserDTO> list;
    }

    @Override
    public List<SuposUserDTO> getUsersFromSupos(String company) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        // default_org_company
        queryMap.put("companyCode",company);
        HttpResponse response = suposRequestManager.suposApiGet(ApiEnum.USER_API.value, headerMap, queryMap);

        UsersResponse usersResponse = JSON.parseObject(response.body(), UsersResponse.class);

        return usersResponse.getList();
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
}
