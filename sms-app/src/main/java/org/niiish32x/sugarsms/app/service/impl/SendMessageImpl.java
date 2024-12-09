package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.cache.UserPhoneCache;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsRequest;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.niiish32x.sugarsms.common.supos.utils.Retrys;
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
@Slf4j
public class SendMessageImpl implements SendMessageService {

    @Resource
    ZubrixSmsProxy zubrixSmsProxy;


    private final String SMS_URL = "http://cloudsms.zubrixtechnologies.com/api/mt/SendSMS?user=%s&password=%s&senderid=%s&channel=%s&DCS=%s&flashsms=%s&number=%s&text=%s&route=%s&DLTTemplateId=%s&PEID=%s";

    private final String SMS_TEXT_TEMPLATE = "KPI %s exceeded to %s Threshold/Limit value %s value Location sugarArea Date/Time%s Dhampur Sugar Mills";

    @Resource
    UserService userService;

    @Resource
    PersonService personService;

    @Resource
    AlertService alertService;

    @Override
    public void sendOneToSms() {
        String number = "+919747934655";
        String text = "xx";
        ZubrixSmsRequest zubrixSmsRequest = new ZubrixSmsRequest();
        zubrixSmsRequest.CreateSMSRequest(number,text);

        Map<String, String> queryParam = buildTestSendQueryParam(zubrixSmsRequest);
        HttpRequest request = HttpRequest.get("http://cloudsms.zubrixtechnologies.com/api/mt/GetBalance");
        request.formStr(queryParam);

        HttpResponse response = request.execute();

        System.out.println(response.body());
    }

    @Override
    public Result sendOneZubrixSms(String number, String text) {
        ZubrixSmsRequest smsRequest = zubrixSmsProxy.buildRequest(number, text);
        String url = zubrixSmsProxy.buildUrl(smsRequest);
        ZubrixSmsResponse messageResponse = zubrixSmsProxy.send(url);
        return messageResponse.getErrorCode() == 0 ? Result.build(messageResponse,ResultCodeEnum.SUCCESS) : Result.build(messageResponse,ResultCodeEnum.FAIL);
    }

    @Override
    public void sendOneToSms(String number, String text) {

    }

    @Override
    public Result sendMessageToSugarSmsUser() {

        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }

        for (SuposUserDTO userDTO : sugasmsUsers) {

            String phoneNumber = UserPhoneCache.cache.getIfPresent(userDTO.getPersonCode());

            if(phoneNumber == null) {
                PersonDTO person = personService.getOnePersonByPersonCode(
                        PersonCodesDTO.builder()
                                .personCodes(Arrays.asList(userDTO.getPersonCode()))
                                .build()
                );
                phoneNumber = person.getPhone();
                UserPhoneCache.cache.put(userDTO.getPersonCode(),person.getPhone());
            }

            try {
                String finalPhoneNumber = phoneNumber;
                Retrys.doWithRetry(()-> sendOneZubrixSms(finalPhoneNumber,"text"), r -> r.isOk(),5,100);
            }catch (Throwable e) {
                String s = String.format("person: %s 未能成功通知到！！！", userDTO.getPersonCode());
                throw new IllegalStateException(s, e);
            }

            log.info("person: {} phone:{} 通知成功",userDTO.getPersonName(),phoneNumber);
//            sendOneSmsMessage(phoneNumber,"text");
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
            PersonDTO person = personService.getOnePersonByPersonCode(
                    PersonCodesDTO.builder()
                            .personCodes(Arrays.asList(userDTO.getPersonCode()))
                            .build()
            );

            sendOneToSms(person.getPhone(),text);
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


    private Map<String,String> buildTestSendQueryParam(ZubrixSmsRequest request) {
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
