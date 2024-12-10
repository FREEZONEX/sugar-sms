package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.SMSMessageRequest;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * SendMessageImpl
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */

@Service
public class SendMessageImpl implements SendMessageService {

    private static Cache<String,String> sugarSmsPersonPhoneCache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build();

    @Resource
    UserService userService;

    @Resource
    PersonService personService;

    @Resource
    AlertService alertService;

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

    @Override
    public void sendOne(String number, String text) {
        SMSMessageRequest smsMessageRequest = new SMSMessageRequest();
        smsMessageRequest.CreateSMSRequest(number,text);

        Map<String, String> queryParam = buildQueryParam(smsMessageRequest);
        HttpRequest request = HttpRequest.get("http://cloudsms.zubrixtechnologies.com/api/mt/GetBalance");
        request.formStr(queryParam);

        HttpResponse response = request.execute();

        System.out.println(response.body());
    }

    @Override
    public Result sendMessageToSugarSmsUser() {

        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }

        for (SuposUserDTO userDTO : sugasmsUsers) {

            String phone = sugarSmsPersonPhoneCache.getIfPresent(userDTO.getPersonCode());

            if(phone != null) {
                sendOne(phone,"text");
            } else {
                PersonDTO person = personService.getOnePersonByPersonCodes(
                        PersonCodesDTO.builder()
                                .personCodes(Arrays.asList(userDTO.getPersonCode()))
                                .build()
                );

                sendOne(person.getPhone(),"text");
                sugarSmsPersonPhoneCache.put(userDTO.getPersonCode(),person.getPhone());
            }

        }

        return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result sendMessageToSugarSmsUser(String text) {
        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }

        for (SuposUserDTO userDTO : sugasmsUsers) {
            PersonDTO person = personService.getOnePersonByPersonCodes(
                    PersonCodesDTO.builder()
                            .personCodes(Arrays.asList(userDTO.getPersonCode()))
                            .build()
            );

            sendOne(person.getPhone(),text);
        }

        return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result sendAlertToSugarSmsUser() {
        List<AlertInfoDTO> alerts = alertService.getAlerts().getData();

        for (AlertInfoDTO alert : alerts) {
            sendMessageToSugarSmsUser(JSON.toJSONString(alert));
        }

        return Result.build("发送完成",ResultCodeEnum.SUCCESS);
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
