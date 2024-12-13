package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.app.cache.UserInfoCache;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.AlertResponse;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.EmailSenderProxy;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.result.ResultCodeEnum;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * AlertServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.10 10:03
 */

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    // 防止重复发送
    static  ConcurrentHashMap <String,String> visited = new ConcurrentHashMap<>();
    // sms + 消息ID
    private final String PHONE_KEY = "sms%s";

    // email + 消息ID
    private final String EMAIL_KEY = "email%s";

    ThreadPoolExecutor threadPoolExecutor
            = new ThreadPoolExecutor(16, // 核心线程数
            32, // 最大线程数
            5, TimeUnit.MINUTES,// 当线程数大于核心线程数时，多余的空闲线程存活的最长时间
            new ArrayBlockingQueue<>(10),
            new ThreadPoolExecutor.CallerRunsPolicy()  // 告警消息为重要任务 不能丢弃
    );

    @Resource
    ZubrixSmsProxy zubrixSmsProxy;


    @Resource
    UserService userService;

    @Resource
    UserInfoCache userInfoCache;

    @Resource
    PersonService personService;

    @Resource
    SuposRequestManager requestManager;


    @Resource
    SendMessageService sendMessageService;

    @Override
    public Result <List<AlertInfoDTO>> getAlertsFromSupos() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_API.value, headerMap, queryMap);
        AlertResponse alertResponse = JSON.parseObject(response.body(), AlertResponse.class);
        return alertResponse.getCode() == 200 ? Result.build(alertResponse.getAlerts(), ResultCodeEnum.SUCCESS) : Result.build(null,ResultCodeEnum.FAIL) ;
    }

    @Override
    public Result notifySugarUserBySms() {

        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isOk()) {
            log.error("获取报警信息异常");
            return Result.build(ResultCodeEnum.FAIL,null);
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            return Result.build(ResultCodeEnum.SUCCESS,null);
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }



        for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {

            String key = String.format(PHONE_KEY,alertInfoDTO.getId());

            if (visited.containsKey(key)) {
                continue;
            }

            visited.put(key,"1");

            String text = zubrixSmsProxy.formatTextContent(alertInfoDTO);
            for (SuposUserDTO userDTO : sugasmsUsers) {

                String phoneNumber = userInfoCache.nameToPhone.getIfPresent(userDTO.getPersonCode());

                if(phoneNumber == null) {
                    PersonDTO person = personService.getOnePersonByPersonCode(
                            PersonCodesDTO.builder()
                                    .personCodes(Arrays.asList(userDTO.getPersonCode()))
                                    .build()
                    );
                    phoneNumber = person.getPhone();
                    userInfoCache.load();
                }



                try {
                    String finalPhoneNumber = phoneNumber;

                    CompletableFuture.supplyAsync(() -> {
                        try {
                            return Retrys.doWithRetry(()-> sendMessageService.sendOneZubrixSmsMessage(finalPhoneNumber,text), r -> r.isOk(),3,100);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });

                }catch (Throwable e) {
                    String s = String.format("person: %s 未能成功通知到！！！", userDTO.getPersonCode());
                    throw new IllegalStateException(s, e);
                }

                log.info("person: {} phone:{} 通知成功",userDTO.getPersonName(),phoneNumber);
            }
        }

        return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result notifySugarUserByEmail() {

        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isOk()) {
            log.error("获取报警信息异常");
            return Result.build(ResultCodeEnum.FAIL,null);
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            return Result.build(ResultCodeEnum.SUCCESS,null);
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
        }



        for (AlertInfoDTO alertInfoDTO : alertInfoDTOS) {

            String key = String.format(EMAIL_KEY,alertInfoDTO.getId());

            if (visited.containsKey(key)) {
                continue;
            }

            visited.put(key,"1");

            String text = zubrixSmsProxy.formatTextContent(alertInfoDTO);
            for (SuposUserDTO userDTO : sugasmsUsers) {

                String email = UserInfoCache.nameToEmail.getIfPresent(userDTO.getPersonCode());

                if(email == null) {
                    PersonDTO person = personService.getOnePersonByPersonCode(
                            PersonCodesDTO.builder()
                                    .personCodes(Arrays.asList(userDTO.getPersonCode()))
                                    .build()
                    );
                    email = person.getEmail();
                    userInfoCache.load();
                }
                if(StringUtils.isNotBlank(email)) {
                    String finalEmail = email;
                    sendMessageService.sendEmail(finalEmail,"sugar-plant-alert",text);
                    log.info("person: {} email:{} 通知成功",userDTO.getPersonName(),email);
                }

            }
        }

        return Result.build(sugasmsUsers,ResultCodeEnum.SUCCESS);
    }

    @Override
    public Result <ZubrixSmsResponse> notifyTest() {
        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isOk()) {
            log.error("获取报警信息异常");
            return Result.build(null,ResultCodeEnum.FAIL);
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            log.info("不存在报警信息 不需要报警");
            return Result.build(null,ResultCodeEnum.SUCCESS);
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            log.warn("不存在sugarsms 角色权限的报警对象");
            return Result.build(null,ResultCodeEnum.SUCCESS);
        }

        AlertInfoDTO alertInfoDTO = alertInfoDTOS.get(0);
        String text = zubrixSmsProxy.formatTextContent(alertInfoDTO);
        SuposUserDTO userDTO = sugasmsUsers.get(0);
        String phoneNumber = userInfoCache.nameToPhone.getIfPresent(userDTO.getPersonCode());

            if(phoneNumber == null) {
                PersonDTO person = personService.getOnePersonByPersonCode(
                        PersonCodesDTO.builder()
                                .personCodes(Arrays.asList(userDTO.getPersonCode()))
                                .build()
                );
                phoneNumber = person.getPhone();
                userInfoCache.load();
            }


        String finalPhoneNumber = phoneNumber;
        ZubrixSmsResponse zubrixSmsResponse = sendMessageService.sendOneZubrixSmsMessage(finalPhoneNumber, text).getData();
        log.info("通知内容 {} ",text);

        log.info("person: {} phone:{} 通知成功",userDTO.getPersonName(),phoneNumber);

        return zubrixSmsResponse.getErrorCode() == 0 ?  Result.build(zubrixSmsResponse,ResultCodeEnum.SUCCESS) : Result.build(zubrixSmsResponse,ResultCodeEnum.FAIL);
    }

}
