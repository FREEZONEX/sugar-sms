package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.service.SuposUserService;
import org.niiish32x.sugarsms.common.supos.aksk.SignUtils;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    @Override
    public List<SuposUserDTO> getSuposUsersFromSupos() {

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        String baseDomain = "http://47.129.0.177:8080/";
        Map<String, String> headerMap = new HashMap<>(16);
        headerMap.put("X-MC-Type", "openAPI");
        headerMap.put("X-MC-Date", sf.format(new Date()));
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        Map<String, String> queryMap = new HashMap<>(16);
        queryMap.put("current", "1");
        queryMap.put("pageSize", "100");
        queryMap.put("companyCode","default_org_company");
        String uri = "/open-api/auth/v2/users";
        String appId = "App_d0f7d746b28aeb634ed82e23f213bdb7";
        String appSecret = "App_d0f7d746b28aeb634ed82e23f213bdb7";
        String ak = "73101af46504b78d84d3b12fab482777";
        String sk = "50fdd25963042ace27d3963cbe78c065";
        SignUtils signUtil = new SignUtils("xx", "xx", ak, sk);
//        headerMap.put("X-MC-AppId", "App_4d4384283dc193f39d6b95b5db1b8940");
//        headerMap.put("X-MC-AppId", "xxx");
        signUtil.signHeaderUseAkSk(uri, "GET", headerMap, queryMap);
//        SignUtils.sign(uri, "GET", headerMap, queryMap);
        HttpResponse response = HttpRequest.get(baseDomain + "" + uri).formStr(queryMap).headerMap(headerMap, true).execute();
        System.out.println(response.body());

//        Map<String,String> headers = new HashMap<>();
//        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
//        headers.put("X-MC-Type","openAPI");
//        headers.put("X-MC-Date",sf.format(new Date()));
//        headers.put("Content-type","application/json;charset=utf-8");
//        headers.put("X-MC-AppId","App_4d4384283dc193f39d6b95b5db1b8940");
////        headers.put("companyCode","default_org_company");
//        Map <String,String> queryParams = new HashMap<>();
//        queryParams.put("companyCode","default_org_company");
//        queryParams.put("current",1);
//        queryParams.put("pageSize","100");
//
//        SignUtil.signHeader("/open-api/auth/v2/users", "GET",headers,queryParams);
//
//        HttpRequest request = HttpRequest
//                .get("http://47.129.0.177:8080/open-api/auth/v2/users")
//                .formStr(queryParams)
//                .addHeaders(headers);
//
//
//        HttpResponse response = request.execute();
//        System.out.println("requestUrl: " + request.getUrl());
//        System.out.println(JSON.toJSONString(response));

        return null;
    }

    @Override
    public List<SuposUserDTO> getCompanyUsersFromSupos() {

        Map<String, String> headerMap = new HashMap<>(16);
        Map<String, String> queryMap = new HashMap<>(16);
        queryMap.put("companyCode","default_org_company");
        HttpResponse response = suposRequestManager.suposApiGet("/open-api/auth/v2/users", headerMap, queryMap);


        System.out.println(response.body());
        return null;
    }
}
