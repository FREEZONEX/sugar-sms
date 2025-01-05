package org.niiish32x.sugarsms.alert.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.common.cache.Cache;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alarm.app.assembler.AlarmAssembler;
import org.niiish32x.sugarsms.alarm.app.command.SavaAlarmCommand;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.alarm.domain.repo.AlarmRepo;
import org.niiish32x.sugarsms.alert.app.command.AlertCommand;
import org.niiish32x.sugarsms.alert.app.command.ProductAlertRecordCommand;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.api.person.dto.PersonDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.cache.UserInfoCache;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.event.AlertEvent;
import org.niiish32x.sugarsms.api.alert.dto.AlertResponse;
import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.queue.AlertMessageQueue;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.manager.thread.GlobalThreadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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

    Set<String> acceptRoles = Sets.newHashSet("sugarsms","canesms","cogensms","chemicalsms","distillerysms","CLsms","mgmtsms","commonsms");

    private final String DEFAULT_COMPANY_CODE = "default_org_company";
    private final String SYSTEM_ROLE_CODE = "systemRole";
    private final String NORMAL_ROLE_CODE = "normalRole";

    private final String SUGAR_ALERT_EMAIL_SUBJECT = "sugar-plant-alert";

    static int maximumPoolSize = 100;
    static int coolPoolSize = 30;

    /**
     * 根据消息表 发送报警 多就下一轮发送即可
     */
    static RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    private static final ThreadPoolExecutor poolExecutor = GlobalThreadManager.getInstance().allocPool(coolPoolSize, maximumPoolSize,
            10 * 60 * 1000, 1000, "sugar-sms-alert-pool", true ,handler);


    // 防止重复发送
    static  ConcurrentHashMap <String,String> visited = new ConcurrentHashMap<>();
    // sms + 消息ID + phone
    private final String PHONE_KEY = "sms%s%s";

    // email + 消息ID + email
    private final String EMAIL_KEY = "email%s%s";


    AlarmAssembler alarmAssembler = AlarmAssembler.INSTANCE;

    @Autowired
    AlarmRepo alarmRepo;

    @Autowired
    AlarmService alarmService;

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
    public void publishAlertEvent() {
        AlertEvent event = new AlertEvent(this);
        publisher.publishEvent(event);
        log.info("发布 报警消息事件");
    }



    @Override
    @Transactional
    public Result alert(AlertRecordEO record) {

       if (record.getType() == MessageType.SMS) {
           Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(record.getPhone(), record.getContent());
           if (smsResp.isSuccess()) {
               log.info("alert: {} {} 发送成功", record.getAlertId(), record.getPhone());
               record.setStatus(true);
               alertRecordRepo.update(record);
           }else {
               log.error("alert: {} {} 发送失败", record.getAlertId(), record.getPhone());
               alertRecordRepo.update(record);
               return Result.error("发送失败");
           }
       } else if (record.getType() == MessageType.EMAIL) {
           boolean res = sendMessageService.sendEmail(record.getEmail(), SUGAR_ALERT_EMAIL_SUBJECT, record.getContent());
           if (res) {
               log.info("alert: {} {} 发送成功", record.getAlertId(), record.getEmail());
               record.setStatus(true);
               alertRecordRepo.update(record);
           }else {
               log.error("alert: {} {} 发送失败", record.getAlertId(), record.getEmail());
               alertRecordRepo.update(record);
               return Result.error("发送失败");
           }
       }


        return Result.success();
    }


    private String getContactInfo(String personCode, Cache<String, String> cache) {
        String contactInfo = cache.getIfPresent(personCode);
        if (contactInfo == null) {
            try {
                PersonDTO person = personService.getOnePersonByPersonCode(
                        PersonCodesDTO.builder()
                                .personCodes(Arrays.asList(personCode))
                                .build()
                ).getData();
                contactInfo = person.getPhone(); // 根据实际需求选择 phone 或 email
                cache.put(personCode, contactInfo);
            } catch (Exception e) {
                log.error("获取用户联系方式失败: {}", e.getMessage(), e);
            }
        }
        return contactInfo;
    }

    private boolean sendNotification(String contactInfo, AlertInfoDTO alertInfoDTO, MessageType messageType, String text, String username) {
        if (StringUtils.isBlank(contactInfo)) {
            return true; // 如果联系方式为空，直接返回成功，避免后续逻辑
        }

        String key = String.format(messageType == MessageType.SMS ? PHONE_KEY : EMAIL_KEY, alertInfoDTO.getId(), contactInfo);
        if (visited.containsKey(key)) {
            log.info("alert 已经{}发送过无需再次通知 {} -> {}", messageType, alertInfoDTO.getId(), contactInfo);
            return true;
        }

        boolean sendSuccess = false;
        try {
            switch (messageType) {
                case SMS:
                    Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(contactInfo, text);
                    sendSuccess = smsResp.isSuccess();
                    break;
                case EMAIL:
                    sendSuccess = sendMessageService.sendEmail(contactInfo, SUGAR_ALERT_EMAIL_SUBJECT, text);
                    break;
            }

            if (sendSuccess) {
                visited.put(key, "1");
            }
        } catch (Exception e) {
            log.error("发送{}通知失败: {}", messageType, e.getMessage(), e);
        }

        AlertRecordEO recordEO = buildAlertRecordEO(alertInfoDTO, username, messageType == MessageType.SMS ? contactInfo : null, messageType == MessageType.EMAIL ? contactInfo : null, messageType, text, sendSuccess);
        boolean saveRes = alertRecordRepo.save(recordEO);

        if (!saveRes) {
            log.error("{} alert: {} {} 数据库 落盘失败", messageType, recordEO.getAlertId(), recordEO.getPhone() != null ? recordEO.getPhone() : recordEO.getEmail());
            return false;
        }

        return true;
    }

    /**
     * 获取所有需要 接收到 通知统治的User
     * @return
     */
    @Override
    public Result<List<SuposUserDTO>>  getAlertUsers() {
        // 获取角色列表并处理异常
        Result<List<RoleSpecDTO>> roleListFromSupos = userService.getRoleListFromSupos(DEFAULT_COMPANY_CODE);
        if (!roleListFromSupos.isSuccess()) {
            return Result.error("Failed to get role list from Supos: " + roleListFromSupos.getMessage());
        }

        List<RoleSpecDTO> roleSpecDTOList = roleListFromSupos.getData();
        if (roleSpecDTOList == null || roleSpecDTOList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<SuposUserDTO> alertUsers = new ArrayList<>(roleSpecDTOList.size() * 10); // 预估用户数量

        for (RoleSpecDTO roleSpecDTO : roleSpecDTOList) {
            if (roleSpecDTO == null || !acceptRoles.contains(roleSpecDTO.getRoleCode()) || roleSpecDTO.getValid() == 0) {
                continue;
            }

            Result<List<SuposUserDTO>> usersFromSupos = userService.getUsersFromSupos(DEFAULT_COMPANY_CODE, roleSpecDTO.getRoleCode());
            if (!usersFromSupos.isSuccess()) {
                return Result.error("Failed to get users from Supos: " + usersFromSupos.getMessage());
            }
            alertUsers.addAll(usersFromSupos.getData());
        }

        return Result.success(alertUsers);
    }


    @Override
    public Boolean cleanAlertPastDays(Integer days) {
        List<AlertRecordEO> alertsBeforeDays = alertRecordRepo.findAlertsBeforeDays(days);

        List<Long> ids = new ArrayList<>();

        for (AlertRecordEO alertRecordEO : alertsBeforeDays) {
            ids.add(alertRecordEO.getId());
        }

        return alertRecordRepo.remove(ids);
    }

    @Override
    public void consumeAlertEvent() {
        log.info("开始 发送alert 报警");

        List<AlertRecordEO> failRecords = alertRecordRepo.findFailRecords();

        for (AlertRecordEO alertRecordEO : failRecords) {
            CompletableFuture.supplyAsync(()-> alert(alertRecordEO), poolExecutor);
//            alert(alertRecordEO);
        }
    }


    @Override
    public Result productAlertRecord(ProductAlertRecordCommand command) {

        AlertInfoDTO alertInfoDTO = command.getAlertInfoDTO();

        try {
            Result<List<SuposUserDTO>> alertUsersResult = getAlertUsers();
            if (!alertUsersResult.isSuccess() || alertUsersResult.getData() == null) {
                log.error("获取告警用户失败: {}", alertUsersResult.getMessage());
                return Result.error(alertUsersResult.getMessage());
            }

            List<SuposUserDTO> userDTOS = alertUsersResult.getData();

            ArrayList<Long> alertsIds = Lists.newArrayList(alertInfoDTO.getId());
            // 批量查询已发送过的 alertId
            List<Long> existingAlertIds = alertRecordRepo.findExistingAlertIds(alertsIds);

            if (existingAlertIds.contains(alertInfoDTO.getId())) {
                log.info("alertId {} 已经发送过", alertInfoDTO.getId());
                return Result.success();
            }

            Result<List<AlarmDTO>> alarmsFromSupos = alarmService.getAlarmsFromSupos(
                    AlarmRequest.builder()
                            .attributeEnName(alertInfoDTO.getSourcePropertyName())
                            .build());

            if (!alarmsFromSupos.isSuccess() || alarmsFromSupos.getData() == null || alarmsFromSupos.getData().isEmpty()) {
                log.error("获取alarmsFromSupos 报警详情信息异常或为空: {}", alarmsFromSupos.getMessage());
                return Result.error(alarmsFromSupos.getMessage());
            }

            AlarmDTO alarmDTO = alarmsFromSupos.getData().get(0);

            SavaAlarmCommand savaAlarmCommand = new SavaAlarmCommand(alarmDTO);
            Result<Boolean> saveResult = alarmService.save(savaAlarmCommand);
            if (!saveResult.isSuccess()) {
                log.error("保存报警信息失败: {}", saveResult.getMessage());
                return Result.error(saveResult.getMessage());
            }

            String text = zubrixSmsProxy.formatTextContent(alertInfoDTO, alarmDTO.getLimitValue());

            List<AlertRecordEO> alertRecords = new ArrayList<>();
            for (SuposUserDTO userDTO : userDTOS) {
                String phoneNumber = getUserPhone(userDTO);
                String email = getUserEmail(userDTO);

                // 验证手机号
                if (StringUtils.isBlank(phoneNumber) || !isValidPhoneNumber(phoneNumber)) {
                    log.warn("用户 {} 的手机号无效: {}", userDTO.getUsername(), phoneNumber);
                }else  {
                    alertRecords.add(buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, false));
                }

                // 验证邮箱
                if (StringUtils.isBlank(email) || !isValidEmail(email)) {
                    log.warn("用户 {} 的邮箱无效: {}", userDTO.getUsername(), email);
                }else {
                    alertRecords.add(buildAlertRecordEO(alertInfoDTO, userDTO.getUsername(), null, email, MessageType.EMAIL, text, false));
                }
            }

            alertRecordRepo.save(alertRecords);

            return Result.success();
        } catch (Exception e) {
            log.error("处理告警记录时发生异常: {}", e.getMessage(), e);
            return Result.error("处理告警记录时发生异常: " + e.getMessage());
        }
    }

    private String getUserEmail (SuposUserDTO userDTO) {
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

        return email;
    }

    private String getUserPhone (SuposUserDTO userDTO) {
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

        return phoneNumber;
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

    private boolean isValidPhoneNumber(String phoneNumber) {
        // 使用正则表达式验证手机号格式
        String phoneRegex = "^\\+?[1-9]\\d{1,14}$";
        return phoneNumber.matches(phoneRegex);
    }

    private boolean isValidEmail(String email) {
        // 使用正则表达式验证邮箱格式
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
}
