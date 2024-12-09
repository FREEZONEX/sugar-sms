package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.niiish32x.sugarsms.app.external.SMSMessageRequest;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * SendMessageImpl
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */

@Service
public class SendMessageImpl implements SendMessageService {
    @Override
    public void sendOne() {
        String number = "+919747934655";
        String text = "xx";
        SMSMessageRequest smsMessageRequest = new SMSMessageRequest();
        smsMessageRequest.CreateSMSRequest(number,text);

        Map<String, String> queryParam = buildQueryParam(smsMessageRequest);
        HttpRequest request = HttpRequest.get("http://cloudsms.zubrixtechnologies.com/api/mt/GetBalance");
        request.formStr(queryParam);

        HttpResponse response = request.execute();

        System.out.println(response.body());
    }


    private Map<String,String> buildQueryParam(SMSMessageRequest request) {
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("APIKey",request.getApiKey());
        queryParams.put("senderid",request.getSenderId());
        queryParams.put("channel",request.getChannel());
        queryParams.put("DCS",request.getDcs());
        queryParams.put("flashsms",request.getFlashSMS());
        queryParams.put("number",request.getNumber());
        queryParams.put("text",request.getText());
        queryParams.put("route",request.getRoute());

        return queryParams;
    }
}
