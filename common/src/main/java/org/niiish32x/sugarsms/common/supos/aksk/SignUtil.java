package org.niiish32x.sugarsms.common.supos.aksk;


import com.alibaba.fastjson2.JSONObject;
//import com.bluetron.eco.sdk.dto.common.SuposConfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.niiish32x.sugarsms.common.supos.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * AK/SK 签名工具类
 */
@Slf4j
@Component
public class SignUtil {

    private AppConfig appConfig;

    private static String APP_ID;
    private static String SECRET_KEY;

    private static String SUPOS_ADDRESS;

    private final static String OPEN_API_URL = "http://192.168.31.223:8000";

    @PostConstruct
    public void init() {
//
//        SignUtil.APP_ID = StringUtils.isEmpty(System.getenv("SUPOS_SUPOS_APP_ACCOUNT_ID")) ?
//                appConfig.getAppId() : System.getenv("SUPOS_SUPOS_APP_ACCOUNT_ID");
//        SignUtil.SECRET_KEY = StringUtils.isEmpty(System.getenv("SUPOS_SUPOS_APP_SECRET_KEY")) ?
//                appConfig.getAppSecret() : System.getenv("SUPOS_SUPOS_APP_SECRET_KEY");
//        SignUtil.SUPOS_ADDRESS = appConfig.getSuposWebAddress();
    }

    /**
     * 通过AK/SK签名 发送HTTP请求调用open api接口
     * <p>部署在supOS k8s环境内部的应用，可以直接调用这个方法</p>
     *
     * @param apiPath  api的请求路径 如:/openapi/users/v1?page=1&per_page=2
     * @param method   HttpMethod
     * @param jsonBody 当method是post put等请求时，所携带的body
     * @return 接口返回
     * @throws Exception e
     */
    public static String doHttpMethod(String apiPath, HttpMethod method, JSONObject jsonBody, Map<String, String> headerMap) throws Exception {
        return doHttpMethod(OPEN_API_URL, apiPath, method, jsonBody, headerMap);
    }

    /**
     * 通过AK/SK签名 发送HTTP请求调用open api接口
     *
     * @param apiBaseURL api的域名或者ip + port
     * @param apiPath    api的请求路径 如:/openapi/users/v1?page=1&per_page=2
     * @param method     HttpMethod
     * @param jsonBody   当method是post put等请求时，所携带的body
     * @return 接口返回
     * @throws Exception e
     */
    public static String doHttpMethod(String apiBaseURL, String apiPath, HttpMethod method, JSONObject jsonBody,
                                      Map<String, String> headerMap) throws Exception {
        log.debug(">>>>>>>>>>>>> AK/SK 请求 apiBaseURL: {} , apiPath: {} , method: {} ,jsonBody: {} <<<<<<<<<<<<<<<<",
                apiBaseURL, apiPath, method, jsonBody);
        SignRequest request = new SignRequest();
        request.setUrl(apiBaseURL, apiPath);
//        request.setAppKey(APP_ID);
//        request.setAppSecret(SECRET_KEY);
        request.setHttpMethod(method);
        if (!CollectionUtils.isEmpty(headerMap)) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (StringUtils.isEmpty(request.getHeaders().get(HttpHeaders.CONTENT_TYPE))) {
            request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        }
        if (null != jsonBody) {
            request.setBody(jsonBody.toString());
        }
        HttpRequestBase requestBase = createSignatureRequest(request);
        CloseableHttpClient client = HttpClients.custom().build();
        HttpResponse response = client.execute(requestBase);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity resEntity = response.getEntity();
        String result = null;
        if (resEntity != null) {
            result = EntityUtils.toString(resEntity, "UTF-8");
        }
        log.debug(">>>>>>>>>>>>> AK/SK 响应状态码: {} , 响应内容: {} <<<<<<<<<<<<<<<<", statusCode, result);
        return result;
    }

    /**
     * 创建一个具有AKSK签名的HTTP CLIENT请求
     *
     * @param request 加签参数
     * @return HttpRequestBase
     */
    private static HttpRequestBase createSignatureRequest(SignRequest request) {
        HttpRequestBase httpRequest; StringEntity entity; HttpMethod httpMethod = request.getHttpMethod();
        String content = request.getBody(); String url = request.getUrl();
        if (httpMethod == HttpMethod.POST) {
            HttpPost postMethod = new HttpPost(url);
            if (StringUtils.isNotEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                postMethod.setEntity(entity);
            }
            httpRequest = postMethod;
        } else if (httpMethod == HttpMethod.PUT) {
            HttpPut putMethod = new HttpPut(url);
            httpRequest = putMethod;
            if (StringUtils.isNotEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                putMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethod.PATCH) {
            HttpPatch patchMethod = new HttpPatch(url);
            httpRequest = patchMethod;
            if (StringUtils.isNotEmpty(content)) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                patchMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethod.GET) {
            httpRequest = new HttpGet(url);
        } else if (httpMethod == HttpMethod.DELETE) {
            httpRequest = new HttpDelete(url);
        } else if (httpMethod == HttpMethod.OPTIONS) {
            httpRequest = new HttpOptions(url);
        } else {
            if (httpMethod != HttpMethod.HEAD) {
                throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
            }
            httpRequest = new HttpHead(url);
        }
        Map<String, String> headers = getSignatureHeader(request);
        Iterator<String> iterator = headers.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            httpRequest.addHeader(key, headers.get(key));
        }
        return httpRequest;
    }

    /**
     * 获取AK/SK加签后的签名头
     *
     * @param request 加签参数
     * @return headers
     */
    public static Map<String, String> getSignatureHeader(SignRequest request) {
        Map<String, String> headers = request.getHeaders();
        //签名源
        StringBuffer sb = new StringBuffer();
        //HttpMethod
        sb.append(request.getHttpMethod()).append("\n");
        //HTTP URI
        sb.append(request.getSignUrl()).append("\n");
        //HTTPContentType
        sb.append(headers.get(HttpHeaders.CONTENT_TYPE)).append("\n");
        //CanonicalQueryString
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            sb.append(request.getQueryString());
        }
        sb.append("\n");
        //CustomHeaders 自定义头  直接换行
        sb.append("\n");
        //log.info(">>>>>>>>>>>>> AK/SK 签名源内容：[{}] <<<<<<<<<<<<<<<<", sb);
        HmacUtils hmacSha256 = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, request.getSecrect());
        String signature = hmacSha256.hmacHex(sb.toString());
        String finalSignature = "Sign " + request.getKey() + "-" + signature;
        if (StringUtils.isEmpty(request.getHeaders().get(HttpHeaders.AUTHORIZATION))) {
            headers.put("Authorization", finalSignature);
        } else {
            headers.put("Authorization", request.getHeaders().get(HttpHeaders.AUTHORIZATION));
        }

        log.debug(">>>>>>>>>>>>> AK/SK 签名结果：{} <<<<<<<<<<<<<<<<", finalSignature);
        return headers;
    }
}