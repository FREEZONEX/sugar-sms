package org.niiish32x.sugarsms.common.request;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.common.supos.aksk.SignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * SuposRequest
 *
 * @author shenghao ni
 * @date 2024.12.08 15:12
 */

@Data
@Component
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class SuposRequestManager implements Serializable {


    private static final Logger LOG = LoggerFactory.getLogger(SuposRequestManager.class);


    @Value("${supos.ak}")
    private String ak;
    @Value("${supos.sk}")
    private String sk;
//    @Value("${supos.supos-address}")
    private final String baseUrl = "http://192.168.2.171:8080";

    private String url;
    private Map<String, String> headerMap;
    private Map<String,String> queryMap;
    private String body;

    public HttpResponse suposApiPut(String uri, Map<String, String> headerMap, Map<String,String> queryMap,String body) {
        SuposRequestManager suposRequest = httpPutBuilder(uri, headerMap, queryMap,body);

        HttpRequest request = new HttpRequest(suposRequest.getUrl())
                .addHeaders(suposRequest.headerMap)
                .body(suposRequest.body)
                .formStr(suposRequest.queryMap)
                .setMethod(Method.POST);

        HttpResponse response = request.execute();

        log.info("url : {}",suposRequest.getUrl());
//        log.info("resp body {}", response.body());

        return response;
    }

    private SuposRequestManager httpPutBuilder(String uri, Map<String, String> headerMap, Map<String, String> queryMap, String body) {
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        headerMap.put("X-MC-Type", "openAPI");
        headerMap.put("X-MC-Date", sf.format(new Date()));
        sign(uri,headerMap,queryMap,Method.PUT.name());

        return SuposRequestManager.builder()
                .url(baseUrl + uri)
                .headerMap(headerMap)
                .queryMap(queryMap)
                .body(body)
                .build();
    }


    public HttpResponse suposApiGet(String uri, Map<String, String> headerMap, Map<String,String> queryMap) {
        SuposRequestManager suposRequest = httpGetBuilder(uri, headerMap, queryMap);
        HttpRequest request = new HttpRequest(suposRequest.getUrl())
                .addHeaders(headerMap)
                .formStr(queryMap);

        HttpResponse response = request.execute();

        log.info("url : {}",suposRequest.getUrl());
//        log.info("resp body {}", response.body());

        return response;
    }

    public HttpResponse suposApiPost(String uri, Map<String, String> headerMap, Map<String,String> queryMap,String body) {
        SuposRequestManager suposRequest = httpPostBuilder(uri, headerMap, queryMap,body);

        HttpRequest request = new HttpRequest(suposRequest.getUrl())
                .addHeaders(suposRequest.headerMap)
                .body(suposRequest.body)
                .formStr(suposRequest.queryMap)
                .setMethod(Method.POST);

        HttpResponse response = request.execute();

        log.info("url : {}",suposRequest.getUrl());
//        log.info("resp body {}", response.body());

        return response;
    }

    private  SuposRequestManager httpGetBuilder (String uri, Map<String, String> headerMap, Map<String,String> queryMap) {
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        headerMap.put("X-MC-Type", "openAPI");
        headerMap.put("X-MC-Date", sf.format(new Date()));
        sign(uri,headerMap,queryMap,Method.GET.name());

        return SuposRequestManager.builder()
                .url(baseUrl + uri)
                .headerMap(headerMap)
                .queryMap(queryMap)
                .build();
    }

    private  SuposRequestManager httpPostBuilder (String uri, Map<String, String> headerMap, Map<String,String> queryMap,String body) {
        headerMap.put("Content-Type", "application/json;charset=utf-8");
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        headerMap.put("X-MC-Type", "openAPI");
        headerMap.put("X-MC-Date", sf.format(new Date()));
        sign(uri,headerMap,queryMap,Method.POST.name());

        return SuposRequestManager.builder()
                .url(baseUrl + uri)
                .headerMap(headerMap)
                .queryMap(queryMap)
                .body(body)
                .build();
    }


    private void sign(String uri, Map<String, String> headerMap, Map<String,String> queryMap,String methodName) {
        SignUtils signUtil = new SignUtils("xx", "xx", ak , sk);
        signUtil.signHeaderUseAkSk(uri, methodName, headerMap, queryMap);
    }


}
