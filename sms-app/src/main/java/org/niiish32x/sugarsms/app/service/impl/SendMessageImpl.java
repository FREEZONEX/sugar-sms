package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.SMSMessageRequest;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * SendMessageImpl
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */

@Service
public class SendMessageImpl implements SendMessageService {

    @Resource
    UserService userService;

    @Resource
    PersonService personService;

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
        List<SuposUserDTO> sugasmsUsers = userService.getUsersFromSupos("default_org_company", "sugarsms");

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }

        for (SuposUserDTO userDTO : sugasmsUsers) {
            PersonDTO person = personService.getOnePersonByPersonCodes(
                    PersonCodesDTO.builder()
                            .personCodes(Arrays.asList(userDTO.getPersonCode()))
                            .build()
            );

            sendOne(person.getIdNumber(),"text");
        }

        return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
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
