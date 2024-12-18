package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.app.cache.UserInfoCache;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.event.AlertEvent;
import org.niiish32x.sugarsms.app.external.AlertResponse;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.queue.AlertMessageQueue;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.result.ResultCode;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
    // sms + 消息ID + phone
    private final String PHONE_KEY = "sms%s%s";

    // email + 消息ID + email
    private final String EMAIL_KEY = "email%s%s";


    @Resource
    AlertRecordRepo alertRecordRepo;

    @Autowired
    AlertMessageQueue alertMessageQueue;


    @Autowired
    ApplicationEventPublisher publisher;

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
    public List<AlertRecordEO> getAllAlertRecords() {
        return alertRecordRepo.find();
    }

    @Override
    public Result <List<AlertInfoDTO>> getAlertsFromSupos() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_API.value, headerMap, queryMap);
        AlertResponse alertResponse = JSON.parseObject(response.body(), AlertResponse.class);
        return alertResponse.getCode() == 200 ? Result.success(alertResponse.getAlerts())  : Result.error("查询报警信息失败") ;
    }

    @Override
    public Result notifySugarUserBySms() {

        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isSuccess()) {
            log.error("获取报警信息异常");
            return Result.error("获取报警信息异常");
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            return Result.success("无需报警信息");
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.success("无需报警信息");
        }

        RateLimiter limiter = RateLimiter.create(3);


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
                    ).getData();
                    phoneNumber = person.getPhone();
                    userInfoCache.load();
                }

                limiter.acquire(1);


                String finalPhoneNumber1 = phoneNumber;
                CompletableFuture.runAsync(() -> {
                    try {
                        String finalPhoneNumber = finalPhoneNumber1;
                        CompletableFuture.supplyAsync(() -> {
                            try {
                                boolean b = limiter.tryAcquire(1);
                                return Retrys.doWithRetry(()-> sendMessageService.sendOneZubrixSmsMessage(finalPhoneNumber,text), r -> r.isSuccess(),3,100);
                            } catch (Throwable e) {
                                throw new RuntimeException(e);
                            }
                        });
                    }catch (Throwable e) {
                        String s = String.format("person: %s 未能成功通知到！！！", userDTO.getPersonCode());
                        log.info(s);
                        throw new IllegalStateException(s, e);
                    }
                    log.info("person: {} phone:{} 通知成功",userDTO.getPersonName(), finalPhoneNumber1);
                }) ;

            }
        }

        return Result.success(sugasmsUsers);
    }

    @Override
    public Result notifySugarUserByEmail() {

        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isSuccess()) {
            return Result.error("获取报警信息异常");
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            return Result.success("无需报警");
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            return Result.success("无需报警");
        }


        RateLimiter limiter = RateLimiter.create(3);



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
                  ).getData();
                  email = person.getEmail();
                  userInfoCache.load();
              }


              if(StringUtils.isNotBlank(email)) {
                  limiter.acquire(1);

                  String finalEmail1 = email;
                  CompletableFuture.runAsync(()->{
                      String finalEmail = finalEmail1;
                      limiter.acquire(1);
                      sendMessageService.sendEmail(finalEmail,"sugar-plant-alert",text);
                      log.info("person: {} email:{} 通知成功",userDTO.getPersonName(), finalEmail1);
                  });
              }
          }
        }


        CompletableFuture.allOf();

        return Result.success(sugasmsUsers);
    }

    @Override
    public Result <ZubrixSmsResponse> notifyTest() {
        Result<List<AlertInfoDTO>> alertsResult = getAlertsFromSupos();

        if(!alertsResult.isSuccess()) {
            log.error("获取报警信息异常");
            return Result.error("获取报警信息异常");
        }

        List<AlertInfoDTO> alertInfoDTOS = alertsResult.getData();

        if(alertInfoDTOS == null || alertInfoDTOS.isEmpty()) {
            log.info("不存在报警信息 不需要报警");
            return Result.success(null);
        }


        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        if(sugasmsUsers.isEmpty()) {
            log.warn("不存在sugarsms 角色权限的报警对象");
            return Result.success(null);
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
                ).getData();
                phoneNumber = person.getPhone();
                userInfoCache.load();
            }


        String finalPhoneNumber = phoneNumber;
        ZubrixSmsResponse zubrixSmsResponse = sendMessageService.sendOneZubrixSmsMessage(finalPhoneNumber, text).getData();
        log.info("通知内容 {} ",text);

        log.info("person: {} phone:{} 通知成功",userDTO.getPersonName(),phoneNumber);

        return zubrixSmsResponse.getErrorCode() == 0 ?  Result.success(zubrixSmsResponse) : Result.error("通知异常");
    }

    @Override
    public void publishAlertEvent() {
        AlertEvent event = new AlertEvent(this);
        publisher.publishEvent(event);
        log.info("发布 报警消息事件");
    }

    @Override
    public Result <Boolean> notifyUserByEmail(SuposUserDTO userDTO,AlertInfoDTO alertInfoDTO ) {

        String email = UserInfoCache.nameToEmail.getIfPresent(userDTO.getPersonCode());

        if (email == null) {
            PersonDTO person = personService.getOnePersonByPersonCode(
                    PersonCodesDTO.builder()
                            .personCodes(Arrays.asList(userDTO.getPersonCode()))
                            .build()
            ).getData();
            email = person.getEmail();
            userInfoCache.load();
        }


        String key = String.format(EMAIL_KEY, alertInfoDTO.getId(),email);

        String text = zubrixSmsProxy.formatTextContent(alertInfoDTO);

        if (visited.containsKey(key)) {
            log.info("alertInfoDTO {} 已发送成功不再重新发送  {}",alertInfoDTO.getId() , email);
            return Result.success(true);
        }

        boolean saveRes = false;
        if (StringUtils.isNotBlank(email)) {
            boolean res = sendMessageService.sendEmail(email, "sugar-plant-alert", text);

            if(res) {
                // 本次发送成功后 进行标记 不再进行二次发送
                visited.put(key, "1");

                AlertRecordEO recordEO = buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), null, email, MessageType.EMAIL, text, true);
                saveRes =  alertRecordRepo.save(recordEO);
            }else {
                AlertRecordEO recordEO = buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), null, email, MessageType.EMAIL, text, false);
                saveRes =  alertRecordRepo.save(recordEO);
            }

            log.info("alert: {} 通知成功 -> email:  {}",alertInfoDTO.getId() , email);

        }

        return saveRes ?  Result.success(saveRes) : Result.error("记录保存失败");
    }

    @Override
    public Result<Boolean> notifyUserBySms(SuposUserDTO userDTO, AlertInfoDTO alertInfoDTO) {

        String text = zubrixSmsProxy.formatTextContent(alertInfoDTO);

        String phoneNumber = userInfoCache.nameToPhone.getIfPresent(userDTO.getPersonCode());

        if(phoneNumber == null) {
            PersonDTO person = personService.getOnePersonByPersonCode(
                    PersonCodesDTO.builder()
                            .personCodes(Arrays.asList(userDTO.getPersonCode()))
                            .build()
            ).getData();
            phoneNumber = person.getPhone();
            userInfoCache.load();
        }

        String key = String.format(PHONE_KEY,alertInfoDTO.getId(),phoneNumber);

        if (visited.containsKey(key)) {
            log.info("alert 已经sms发送过无需再次通知 {} -> {}",alertInfoDTO.getId(),phoneNumber);
            return Result.success(true);
        }

        Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(phoneNumber, text);

        if (smsResp.isSuccess()) {
            visited.put(key,"1");
            log.info("alert {} sms 通知成功 -> {}",alertInfoDTO.getId(),phoneNumber);
        }


        boolean saveRes;

        if (smsResp.isSuccess()) {
            AlertRecordEO recordEO = buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, true);
            saveRes = alertRecordRepo.save(recordEO);
        }else {
            AlertRecordEO recordEO = buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, false);
            saveRes =  alertRecordRepo.save(recordEO);
        }



        return saveRes ? Result.success(saveRes) : Result.error("记录保存失败");
    }

    @Override
    public void consumeAlertEvent() {
        Result<List<SuposUserDTO>> res = userService.getUsersFromSupos("default_org_company", "sugarsms");

        List<SuposUserDTO> sugasmsUsers = res.getData();

        RateLimiter limiter = RateLimiter.create(5);

        while (!alertMessageQueue.isEmpty()) {
            AlertInfoDTO alertInfoDTO = alertMessageQueue.poll();
            int n = sugasmsUsers.size();

            for (int i = 0 ; i < n ; i++) {
                limiter.acquire();
                SuposUserDTO userDTO = sugasmsUsers.get(i);
                CompletableFuture.supplyAsync(() -> notifyUserBySms(userDTO,alertInfoDTO));
                CompletableFuture.supplyAsync(()->notifyUserByEmail(userDTO,alertInfoDTO)) ;
            }

            CompletableFuture.allOf();

        }


         for (AlertRecordEO alertRecordEO :  alertRecordRepo.find(MessageType.SMS, false)) {
             Result<ZubrixSmsResponse> smsResponseResult = sendMessageService.sendOneZubrixSmsMessage(alertRecordEO.getPhone(), alertRecordEO.getContent());
             if (smsResponseResult.isSuccess()) {
                 alertRecordEO.setSendTime(new Date());
                 alertRecordEO.setStatus(true);
                 alertRecordRepo.save(alertRecordEO);
             }
         }

         for (AlertRecordEO alertRecordEO :  alertRecordRepo.find(MessageType.EMAIL, false)) {
             boolean sendRes = sendMessageService.sendEmail(alertRecordEO.getEmail(), "sugar-plant-alert", alertRecordEO.getContent());
             if (sendRes) {
                 alertRecordEO.setSendTime(new Date());
                 alertRecordEO.setStatus(true);
                 alertRecordRepo.save(alertRecordEO);
             }
         }
    }

    private AlertRecordEO buildAlertRecordEO(AlertInfoDTO alertInfoDTO,String username,String phone,String email,MessageType type,String text,Boolean status) {
        if (type == MessageType.SMS) {
            return AlertRecordEO.builder()
                    .type(MessageType.SMS)
                    .alertId(alertInfoDTO.getId())
                    .username(username)
                    .content(text)
                    .sendTime(new Date())
                    .phone(phone)
                    .status(status)
                    .build();
        }else {
            return AlertRecordEO.builder()
                    .type(MessageType.EMAIL)
                    .alertId(alertInfoDTO.getId())
                    .username(username)
                    .content(text)
                    .sendTime(new Date())
                    .email(email)
                    .status(status)
                    .build();
        }
    }
}
