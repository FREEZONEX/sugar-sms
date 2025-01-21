package org.niiish32x.sugarsms.alert.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alarm.app.assembler.AlarmAssembler;
import org.niiish32x.sugarsms.alarm.app.command.SaveAlarmCommand;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.repo.AlarmRepo;
import org.niiish32x.sugarsms.alert.app.command.ProduceAlertRecordCommand;
import org.niiish32x.sugarsms.alert.app.command.SaveAlertCommand;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRepo;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.proxy.AlertContentBuilder;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.api.alert.dto.AlertResponse;
import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.suposperson.app.SuposPersonService;
import org.niiish32x.sugarsms.suposperson.app.command.SavePersonCommand;
import org.niiish32x.sugarsms.suposperson.app.external.PersonPageQueryRequest;
import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;
import org.niiish32x.sugarsms.suposperson.domain.repo.SuposPersonRepo;
import org.niiish32x.sugarsms.user.app.UserService;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.common.enums.UserRoleEnum;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.manager.thread.GlobalThreadManager;
import org.niiish32x.sugarsms.user.app.external.UserPageQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

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

    static int maximumPoolSize = 300;
    static int coolPoolSize = 100;

    /**
     * 根据消息表 发送报警 多就下一轮发送即可
     */
    static RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    private static final ThreadPoolExecutor poolExecutor = GlobalThreadManager.getInstance().allocPool(coolPoolSize, maximumPoolSize,
            10 * 60 * 1000, 3000, "sugar-sms-alert-pool", true ,handler);


    private static final Cache<String,AlarmEO> ALARM_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .initialCapacity(10)
            .maximumSize(30)
            .build();

    /**
     * 缓存报警用户信息 5秒后 过期
     */
    private static final Cache<String,List<SuposUserDTO>> USER_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .initialCapacity(10)
            .maximumSize(30)
            .build();

    /**
     * 缓存人员信息
     * PersonCode --- SuposPersonEO
     * 写入后保存 10秒
     */
    private static final Cache<String,SuposPersonEO> PERSON_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .initialCapacity(200)
            .maximumSize(1000)
            .build();

    AlarmAssembler alarmAssembler = AlarmAssembler.INSTANCE;

    @Autowired
    AlarmRepo alarmRepo;

    @Autowired
    AlarmService alarmService;

    @Resource
    AlertRecordRepo alertRecordRepo;



    @Autowired
    ApplicationEventPublisher publisher;

    @Resource
    ZubrixSmsProxy zubrixSmsProxy;


    @Resource
    UserService userService;

    @Resource
    SuposPersonService suposPersonService;

    @Resource
    SuposRequestManager requestManager;

    @Autowired
    SuposPersonRepo suposPersonRepo;

    @Autowired
    AlertRepo alertRepo;


    @Override
    public Result <List<AlertInfoDTO>> getAlertsFromSupos() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_API.value, headerMap, queryMap);
        AlertResponse alertResponse = JSON.parseObject(response.body(), AlertResponse.class);
        return alertResponse.getCode() == 200 ? Result.success(alertResponse.getAlerts())  : Result.error("查询报警信息失败") ;
    }

    @Override
    public Result<Boolean> saveAlert(SaveAlertCommand command) {
        AlertInfoDTO alertInfoDTO = command.getAlertInfoDTO();

        AlertEO alertEO = AlertEO.builder()
                .alertId(alertInfoDTO.getId())
                .alertName(alertInfoDTO.getAlertName())
                .showName(alertInfoDTO.getShowName())
                .priority(alertInfoDTO.getPriority())
                .source(alertInfoDTO.getSource())
                .sourceShowName(alertInfoDTO.getSourceShowName())
                .sourcePropertyName(alertInfoDTO.getSourcePropertyName())
                .sourcePropShowName(alertInfoDTO.getSourcePropShowName())
                .description(alertInfoDTO.getDescription())
                .newValue(alertInfoDTO.getNewValue())
                .valType(alertInfoDTO.getValType())
                .oldValue(alertInfoDTO.getOldValue())
                .finishGenerateAlertRecord(false)
                .build();

        boolean res = alertRepo.saveOrUpdate(alertEO);

        return res ? Result.success(true) : Result.error("save alert error! alertId: "+alertEO.getAlertId());
    }


    /**
     * 获取所有需要 接收到 通知统治的User
     * @return
     */
    @Override
    public Result<List<SuposUserDTO>>  getAlertUsers() {
        List<SuposUserDTO> alertUsers = null;

        alertUsers = USER_CACHE.getIfPresent("alert");

        if (alertUsers != null) {
            return Result.success(alertUsers);
        }

        // 获取角色列表并处理异常
        Result<List<RoleSpecDTO>> roleListFromSupos = userService.getRoleListFromSupos(CompanyEnum.DEFAULT.value);
        if (!roleListFromSupos.isSuccess()) {
            return Result.error("Failed to get role list from Supos: " + roleListFromSupos.getMessage());
        }

        List<RoleSpecDTO> roleSpecDTOList = roleListFromSupos.getData();
        if (roleSpecDTOList == null || roleSpecDTOList.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        alertUsers = new ArrayList<>(roleSpecDTOList.size() * 10); // 预估用户数量

        for (RoleSpecDTO roleSpecDTO : roleSpecDTOList) {
            if (roleSpecDTO == null || !UserRoleEnum.isAlertRole(roleSpecDTO.getRoleCode()) || roleSpecDTO.getValid() == 0) {
                continue;
            }

            UserPageQueryRequest userPageQueryRequest = UserPageQueryRequest.builder()
                    .companyCode(CompanyEnum.DEFAULT.value)
                    .roleCode(roleSpecDTO.getRoleCode())
                    .getAll(true)
                    .build();
            Result<List<SuposUserDTO>> usersFromSupos = userService.getUsersFromSupos(userPageQueryRequest);
            if (!usersFromSupos.isSuccess()) {
                return Result.error("Failed to get users from Supos: " + usersFromSupos.getMessage());
            }
            alertUsers.addAll(usersFromSupos.getData());
        }

        USER_CACHE.put("alert",alertUsers);

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
    public boolean productAlertRecord(AlertEO alertEO) {

        try {
            Result<List<SuposUserDTO>> alertUsersResult = getAlertUsers();
            if (!alertUsersResult.isSuccess() || alertUsersResult.getData() == null) {
                log.error("获取告警用户失败: {}", alertUsersResult.getMessage());
                return false;
            }

            List<SuposUserDTO> userDTOS = alertUsersResult.getData();

            AlarmEO alarmEO = null;

            alarmEO = ALARM_CACHE.getIfPresent(alertEO.getSourcePropertyName());

            if (alarmEO == null) {
                alarmEO = alarmRepo.findWithAttributeEnName(alertEO.getSourcePropertyName());
            }

            if (alarmEO == null) {
                Result<List<AlarmDTO>> alarmsFromSupos = alarmService.getAlarmsFromSupos(
                        AlarmRequest.builder()
                                .attributeEnName(alertEO.getSourcePropertyName())
                                .build());

                if (!alarmsFromSupos.isSuccess() || alarmsFromSupos.getData() == null || alarmsFromSupos.getData().isEmpty()) {
                    log.error("获取alarmsFromSupos 报警详情信息异常或为空: {}", alarmsFromSupos.getMessage());
                    return false;
                }

                AlarmDTO alarmDTO = alarmsFromSupos.getData().get(0);
                SaveAlarmCommand saveAlarmCommand = new SaveAlarmCommand(alarmDTO);
                Result<Boolean> saveRes = alarmService.save(saveAlarmCommand);

                if (!saveRes.isSuccess()) {
                    log.error("保存alarmsFromSupos 报警详情信息异常: {}", saveRes.getMessage());
                    return false;
                }

                alarmEO = alarmRepo.findWithAttributeEnName(alertEO.getSourcePropertyName());
            }

            ALARM_CACHE.put(alertEO.getSourcePropertyName(), alarmEO);

//            String text = zubrixSmsProxy.formatTextContent(AlertContentBuilder.builder()
//                            .sourcePropertyName(alertEO.getSourcePropertyName())
//                            .newValue(alertEO.getNewValue())
//                            .source(alertEO.getSource())
//                            .startDataTimestamp(alertEO.getStartDataTimestamp())
//                            .limitValue(alarmEO.getLimitValue())
//                    .build());

            List<AlertRecordEO> alertRecords = new ArrayList<>();
            for (SuposUserDTO userDTO : userDTOS) {
                AlarmEO finalAlarmEO = alarmEO;
                CompletableFuture<List<AlertRecordEO>> listCompletableFuture = CompletableFuture.supplyAsync(() -> prepareAlertRecord(userDTO, alertEO, finalAlarmEO), poolExecutor);
                listCompletableFuture.join();
                alertRecords.addAll(listCompletableFuture.get());
            }

            alertRecordRepo.save(alertRecords);

            return true;
        } catch (Exception e) {
            log.error("处理告警记录时发生异常: {}", e.getMessage(), e);
            return false;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Result productAlertRecord(ProduceAlertRecordCommand command) {

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
//                log.info("alertId {} 已经发送过", alertInfoDTO.getId());
                return Result.success();
            }

            AlarmEO alarmEO = null;

            alarmEO = ALARM_CACHE.getIfPresent(alertInfoDTO.getSourcePropertyName());

            if (alarmEO == null) {
                alarmEO = alarmRepo.findWithAttributeEnName(alertInfoDTO.getSourcePropertyName());
            }

            if (alarmEO == null) {
                Result<List<AlarmDTO>> alarmsFromSupos = alarmService.getAlarmsFromSupos(
                        AlarmRequest.builder()
                                .attributeEnName(alertInfoDTO.getSourcePropertyName())
                                .build());

                if (!alarmsFromSupos.isSuccess() || alarmsFromSupos.getData() == null || alarmsFromSupos.getData().isEmpty()) {
                    log.error("获取alarmsFromSupos 报警详情信息异常或为空: {}", alarmsFromSupos.getMessage());
                    return Result.error(alarmsFromSupos.getMessage());
                }

                AlarmDTO alarmDTO = alarmsFromSupos.getData().get(0);
                SaveAlarmCommand saveAlarmCommand = new SaveAlarmCommand(alarmDTO);
                Result<Boolean> saveRes = alarmService.save(saveAlarmCommand);

                if (!saveRes.isSuccess()) {
                    log.error("保存alarmsFromSupos 报警详情信息异常: {}", saveRes.getMessage());
                    return Result.error(saveRes.getMessage());
                }

                alarmEO = alarmRepo.findWithAttributeEnName(alertInfoDTO.getSourcePropertyName());
            }

            ALARM_CACHE.put(alertInfoDTO.getSourcePropertyName(), alarmEO);

            String text = zubrixSmsProxy.formatTextContent(alertInfoDTO, alarmEO.getLimitValue());

            List<AlertRecordEO> alertRecords = new ArrayList<>();
            for (SuposUserDTO userDTO : userDTOS) {
                CompletableFuture<List<AlertRecordEO>> listCompletableFuture = CompletableFuture.supplyAsync(() -> prepareAlertRecord(userDTO, alertInfoDTO, text), poolExecutor);
                alertRecords.addAll(listCompletableFuture.get());
            }

            alertRecordRepo.save(alertRecords);

            return Result.success();
        } catch (Exception e) {
            log.error("处理告警记录时发生异常: {}", e.getMessage(), e);
            return Result.error("处理告警记录时发生异常: " + e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private List<AlertRecordEO> prepareAlertRecord(SuposUserDTO userDTO, AlertEO alertEO, AlarmEO alarmEO) {

        String text = zubrixSmsProxy.formatTextContent(AlertContentBuilder.builder()
                .sourcePropertyName(alertEO.getSourcePropertyName())
                .newValue(alertEO.getNewValue())
                .source(alertEO.getSource())
                .startDataTimestamp(alertEO.getStartDataTimestamp())
                .limitValue(alarmEO.getLimitValue())
                .build());

        List<AlertRecordEO> alertRecords = new ArrayList<>();

        String phoneNumber = null;
        String email = null;

        SuposPersonEO personEO = null;

        personEO = PERSON_CACHE.getIfPresent(userDTO.getPersonCode());

        if (personEO == null ) {
            personEO = suposPersonRepo.findByCode(userDTO.getPersonCode());
        }else if (personEO.getUser().getModifyTime() != null &&  personEO.getUser().getModifyTime() != userDTO.getModifyTime()) {
            /**
             * User 并非最新的User 删除本地缓存
             */
            PERSON_CACHE.invalidate(userDTO.getPersonCode());
            suposPersonRepo.softRemove(personEO);
        }

        if (personEO == null || personEO.getDeleted() || personEO.getUser().getModifyTime() != userDTO.getModifyTime() ) {
            synchronized (this){
                if (personEO == null || personEO.getDeleted()) {
                    PersonPageQueryRequest request = PersonPageQueryRequest.builder()
                            .companyCode(CompanyEnum.DEFAULT.value)
                            .hasBoundUser(true)
                            .username(userDTO.getUsername())
                            .build();
                    Result<List<SuposPersonDTO>> peronFromSupos = suposPersonService.searchPeronFromSupos(request);

                    if (!peronFromSupos.isSuccess() || peronFromSupos.getData() == null || peronFromSupos.getData().isEmpty()) {
                        log.error("获取用户信息失败: {}", userDTO.getPersonCode());
                    }


                    SavePersonCommand savePersonCommand = new SavePersonCommand(peronFromSupos.getData().get(0));
                    Result savePerson = suposPersonService.savePerson(savePersonCommand);
                    if (!savePerson.isSuccess()) {
                        log.error("保存用户信息失败: {}", savePerson.getMessage());
                    }
                }
            }

            personEO = suposPersonRepo.findByCode(userDTO.getPersonCode());
        }

        Preconditions.checkArgument(personEO != null, "personEO is null " + userDTO.getUsername());
        PERSON_CACHE.put(userDTO.getPersonCode(), personEO);
        phoneNumber = personEO.getPhone() == null ? null : personEO.getPhone().trim();
        email =  personEO.getEmail() == null ? null : personEO.getEmail().trim();

        // 验证手机号
        if ( StringUtils.isNotBlank(phoneNumber) && isValidPhoneNumber(Objects.requireNonNull(phoneNumber))) {
            alertRecords.add(buildAlertRecordEO(alertEO.getAlertId(), userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, false,alarmEO));
        }

        // 验证邮箱
        if (StringUtils.isNotBlank(email) && isValidEmail(email)) {
            alertRecords.add(buildAlertRecordEO(alertEO.getAlertId(), userDTO.getUsername(), null, email, MessageType.EMAIL, text, false,alarmEO));
        }

        return alertRecords;
    }


    @Deprecated
    private List<AlertRecordEO> prepareAlertRecord(SuposUserDTO userDTO, AlertInfoDTO alertInfoDTO, String text) {
        List<AlertRecordEO> alertRecords = new ArrayList<>();

        String phoneNumber = null;
        String email = null;

        SuposPersonEO personEO = null;

        personEO = PERSON_CACHE.getIfPresent(userDTO.getPersonCode());

        if (personEO == null ) {
            personEO = suposPersonRepo.findByCode(userDTO.getPersonCode());
        }else if (personEO.getUser().getModifyTime() != null &&  personEO.getUser().getModifyTime() != userDTO.getModifyTime()) {
            /**
             * User 并非最新的User 删除本地缓存
             */
            PERSON_CACHE.invalidate(userDTO.getPersonCode());
            suposPersonRepo.softRemove(personEO);
        }

        if (personEO == null || personEO.getDeleted() || personEO.getUser().getModifyTime() != userDTO.getModifyTime() ) {
            synchronized (this){
                if (personEO == null || personEO.getDeleted()) {
                    PersonPageQueryRequest request = PersonPageQueryRequest.builder()
                            .companyCode(CompanyEnum.DEFAULT.value)
                            .hasBoundUser(true)
                            .username(userDTO.getUsername())
                            .build();
                    Result<List<SuposPersonDTO>> peronFromSupos = suposPersonService.searchPeronFromSupos(request);

                    if (!peronFromSupos.isSuccess() || peronFromSupos.getData() == null || peronFromSupos.getData().isEmpty()) {
                        log.error("获取用户信息失败: {}", userDTO.getPersonCode());
                    }


                    SavePersonCommand savePersonCommand = new SavePersonCommand(peronFromSupos.getData().get(0));
                    Result savePerson = suposPersonService.savePerson(savePersonCommand);
                    if (!savePerson.isSuccess()) {
                        log.error("保存用户信息失败: {}", savePerson.getMessage());
                    }
                }
            }

            personEO = suposPersonRepo.findByCode(userDTO.getPersonCode());
        }

        Preconditions.checkArgument(personEO != null, "personEO is null " + userDTO.getUsername());
        PERSON_CACHE.put(userDTO.getPersonCode(), personEO);
        phoneNumber = personEO.getPhone() == null ? null : personEO.getPhone().trim();
        email =  personEO.getEmail() == null ? null : personEO.getEmail().trim();

        // 验证手机号
        if ( StringUtils.isNotBlank(phoneNumber) && isValidPhoneNumber(Objects.requireNonNull(phoneNumber))) {
            alertRecords.add(buildAlertRecordEO(alertInfoDTO.getId(), userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, false));
        }

        // 验证邮箱
        if (StringUtils.isNotBlank(email) && isValidEmail(email)) {
            alertRecords.add(buildAlertRecordEO(alertInfoDTO.getId(), userDTO.getUsername(), null, email, MessageType.EMAIL, text, false));
        }

        return alertRecords;
    }



    private AlertRecordEO buildAlertRecordEO(Long alertId,String username,String phone,String email,MessageType type,String text,Boolean status,AlarmEO alarmEO) {
        if (type == MessageType.SMS) {
            return AlertRecordEO.builder()
                    .type(MessageType.SMS)
                    .alertId(alertId)
                    .username(username)
                    .content(text)
                    .sendTime(new Date())
                    .phone(phone)
                    .status(status)
                    .alarm(alarmEO)
                    .alarmId(Long.valueOf(alarmEO.getAlarmId()))
                    .build();
        }else {
            return AlertRecordEO.builder()
                    .type(MessageType.EMAIL)
                    .alertId(alertId)
                    .username(username)
                    .content(text)
                    .sendTime(new Date())
                    .email(email)
                    .status(status)
                    .alarm(alarmEO)
                    .alarmId(alarmEO.getId())
                    .build();
        }
    }

    private AlertRecordEO buildAlertRecordEO(Long alertId,String username,String phone,String email,MessageType type,String text,Boolean status) {
        if (type == MessageType.SMS) {
            return AlertRecordEO.builder()
                    .type(MessageType.SMS)
                    .alertId(alertId)
                    .username(username)
                    .content(text)
                    .sendTime(new Date())
                    .phone(phone)
                    .status(status)
                    .build();
        }else {
            return AlertRecordEO.builder()
                    .type(MessageType.EMAIL)
                    .alertId(alertId)
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
