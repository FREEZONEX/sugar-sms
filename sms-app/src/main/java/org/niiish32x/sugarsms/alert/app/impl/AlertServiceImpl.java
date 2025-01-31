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
import org.niiish32x.sugarsms.alert.app.assmbler.AlertRecordAssembler;
import org.niiish32x.sugarsms.alert.app.command.ProduceAlertRecordCommand;
import org.niiish32x.sugarsms.alert.app.command.SaveAlertCommand;
import org.niiish32x.sugarsms.alert.app.query.AlertRecordsCountQuery;
import org.niiish32x.sugarsms.alert.app.query.AlertRecordsQuery;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRepo;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alert.dto.*;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserDTO;
import org.niiish32x.sugarsms.api.user.dto.SuposUserRoleDTO;
import org.niiish32x.sugarsms.app.event.AlertRecordChangeEvent;
import org.niiish32x.sugarsms.app.proxy.AlertContentBuilder;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.common.event.EventBus;
import org.niiish32x.sugarsms.common.result.PageResult;
import org.niiish32x.sugarsms.common.utils.Retrys;
import org.niiish32x.sugarsms.message.app.SendMessageService;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
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
import org.niiish32x.sugarsms.user.domain.entity.UserEO;
import org.niiish32x.sugarsms.user.domain.entity.UserRoleEO;
import org.niiish32x.sugarsms.user.domain.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * AlertServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.10 10:03
 */

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final String SUGAR_ALERT_EMAIL_SUBJECT = "sugar-plant-alert";

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


    @Autowired
    SendMessageService sendMessageService;

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

    @Autowired
    UserRepo userRepo;

    AlertRecordAssembler alertRecordAssembler = AlertRecordAssembler.INSTANCE;

    @Override
    public boolean alert(Long id, MessageType messageType, String message, String receiver) {
        boolean res = false;

        if (messageType == MessageType.SMS) {
            Result<ZubrixSmsResponse> smsResp = sendMessageService.sendOneZubrixSmsMessage(receiver, message);
            res = smsResp.isSuccess();
        } else if (messageType == MessageType.EMAIL) {
            res = sendMessageService.sendEmail(receiver, SUGAR_ALERT_EMAIL_SUBJECT, message);
        }

        if (res) {
            boolean updateRes = alertRecordRepo.updateStatusById(id,true);
            if (!updateRes) {
                log.error("alert: id:{} {} 更新状态失败", id , receiver);
                return false;
            }
        }else {
            log.error("alert: id:{} {} 发送失败", id, receiver);
            return false;
        }


        EventBus.publishEvent(new AlertRecordChangeEvent(this,String.format(">>> alert send success!  messageType:%s  receiver:%s",messageType.name(),receiver)));

        return true;
    }

    @Override
    public Result <List<AlertInfoDTO>> getAlertsFromSupos() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        try {
            HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_API.value, headerMap, queryMap);
            AlertResponse alertResponse = JSON.parseObject(response.body(), AlertResponse.class);
            return alertResponse.getCode() == 200 ? Result.success(alertResponse.getAlerts())  : Result.error("get alert info error") ;
        }catch (Exception e) {
            return Result.error("get alert info error",e);
        }
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
                .startDataTimestamp(alertInfoDTO.getStartDataTimestamp())
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

//        alertUsers = USER_CACHE.getIfPresent("alert");

//        if (alertUsers != null) {
//            return Result.success(alertUsers);
//        }

        // 获取角色列表并处理异常
//        Result<List<RoleSpecDTO>> roleListFromSupos = userService.getRoleListFromSupos(CompanyEnum.DEFAULT.value);
//
//        if (!roleListFromSupos.isSuccess()) {
//            return Result.error("Failed to get role list from Supos: " + roleListFromSupos.getMessage());
//        }
//
//        List<RoleSpecDTO> roleSpecDTOList = roleListFromSupos.getData();
//        if (roleSpecDTOList == null || roleSpecDTOList.isEmpty()) {
//            return Result.success(new ArrayList<>());
//        }

//        alertUsers = new ArrayList<>(roleSpecDTOList.size() * 10); // 预估用户数量

        alertUsers = new ArrayList<>( ); // 预估用户数量

        Result<List<SuposUserDTO>> usersFromSupos = userService.getUsersFromSupos(
                UserPageQueryRequest.builder()
                        .getAll(true)
                        .build()
        );

        log.info(">>> supos user get total number  >>>>>>>>>>> \n {}",usersFromSupos.getData().size());


        for (SuposUserDTO userDTO : usersFromSupos.getData()) {

            if (userDTO.getUserRoleList() == null || userDTO.getUserRoleList().isEmpty()) {
                continue;
            }


            List<SuposUserRoleDTO> userRoleListDTO = userDTO.getUserRoleList();

            List<UserRoleEO> userRoleEOList = new ArrayList<>();

            for (SuposUserRoleDTO roleDTO : userRoleListDTO) {

                userRoleEOList.add(UserRoleEO.builder()
                                .total(roleDTO.getTotal())
                                .name(roleDTO.getName())
                                .showName(roleDTO.getShowName())
                                .description(roleDTO.getDescription())
                        .build());

            }

            boolean saveUserRes = userRepo.save(UserEO.builder()
                    .username(userDTO.getUsername())
                    .userDesc(userDTO.getUserDesc())
                    .accountType(userDTO.getAccountType())
                    .lockStatus(userDTO.getLockStatus())
                    .valid(userDTO.getValid())
                    .personCode(userDTO.getPersonCode())
                    .personName(userDTO.getPersonName())
                    .avatar(userDTO.getAvatar())
                    .modifyTime(userDTO.getModifyTime())
                    .createTime(userDTO.getCreateTime())
                    .userRoleList(userRoleEOList)
                    .build());

            for (SuposUserRoleDTO roleDTO : userDTO.getUserRoleList()) {
                 if (UserRoleEnum.isAlertRole(roleDTO.getName())){
                     alertUsers.add(userDTO);
                     break;
                 }
            }
        }

//        for (RoleSpecDTO roleSpecDTO : roleSpecDTOList) {
//            if (roleSpecDTO == null || !UserRoleEnum.isAlertRole(roleSpecDTO.getRoleCode()) || roleSpecDTO.getValid() == 0) {
//                continue;
//            }
//
//            UserPageQueryRequest userPageQueryRequest = UserPageQueryRequest.builder()
//                    .companyCode(CompanyEnum.DEFAULT.value)
//                    .roleCode(roleSpecDTO.getRoleCode())
//                    .getAll(true)
//                    .build();
//            usersFromSupos = userService.getUsersFromSupos(userPageQueryRequest);
//            if (!usersFromSupos.isSuccess()) {
//                return Result.error("Failed to get users from Supos: " + usersFromSupos.getMessage());
//            }
//            alertUsers.addAll(usersFromSupos.getData());
//        }

//        USER_CACHE.put("alert",alertUsers);



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

//            log.info("users to info are:\n {} ",JSON.toJSONString(userDTOS));

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

            List<AlertRecordEO> alertRecords = new ArrayList<>();
            for (SuposUserDTO userDTO : userDTOS) {
                AlarmEO finalAlarmEO = alarmEO;
                CompletableFuture<List<AlertRecordEO>> listCompletableFuture = CompletableFuture.supplyAsync(() -> prepareAlertRecord(userDTO, alertEO, finalAlarmEO), poolExecutor);
                listCompletableFuture.join();
                alertRecords.addAll(listCompletableFuture.get());
            }

            for (AlertRecordEO alertRecordEO : alertRecords) {
                // 记录一定要存进去 不然后续整个链路都会受到影响 宁愿多花一些时间
                Retrys.doWithRetry(()-> alertRecordRepo.saveUniByReceiver(alertRecordEO) , r -> r ,10 ,2 * 1000);
            }

//            alertRecordRepo.saveUniByReceiver(alertRecords);

            EventBus.publishEvent(new AlertRecordChangeEvent(this, String.format(">>>> batch alert record generate  alertId:%s  alertName:%s >>>>",alertEO.getAlertId(),alertEO.getAlertName())));

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

    @Override
    public Result<List<AlertRecordDTO>> queryAlertRecords() {
        List<AlertRecordEO> alertRecordEOS = alertRecordRepo.find();

        List<AlertRecordDTO> alertRecordDTOS = new ArrayList<>();


        for (AlertRecordEO alertRecordEO : alertRecordEOS) {
            AlertRecordDTO dto = alertRecordAssembler.toDTO(alertRecordEO);
            alertRecordDTOS.add(dto);
        }

        return Result.success(alertRecordDTOS);
    }

    @Override
    public Result ackAlerts() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        Map<String,Object> jsonMap = new HashMap<>();

        List<AlertEO> alertEOS = alertRepo.find();

        if (alertEOS == null || alertEOS.isEmpty()) {
            return Result.success("无记录 不需要确认");
        }

        AlertAckRequest alertAckRequest = AlertAckRequest.builder()
                .ackAll("true") // 确认所有
                .userName("admin")
                .build();

        List <String> fullNamesParamlist = new ArrayList<>();

        for (AlertEO alertEO : alertEOS) {
            String s = alertEO.getAlertId() +  alertEO.getAlertName();
            fullNamesParamlist.add(s);
        }

        jsonMap.put("ackAll",true);
        jsonMap.put("userName",alertAckRequest.getUserName());


        jsonMap.put("fullNames",fullNamesParamlist);

        HttpResponse response = requestManager.suposApiPost(ApiEnum.ALERT_ACK_POST_API.value, headerMap, queryMap,JSON.toJSONString(jsonMap));

        if (response.getStatus() == 200) {
            return Result.success("确认报警完成");
        }

        AlertAckResponse alertAckResponse = JSON.parseObject(response.body(), AlertAckResponse.class);
        return Result.error(JSON.toJSONString(alertAckResponse)) ;
    }

    @Override
    public Result<PageResult<AlertRecordDTO>> searchAlertRecord(AlertRecordsQuery query) {

        PageResult<AlertRecordEO> pageRes = alertRecordRepo.page(query.getPage(), query.getLimit());

        List<AlertRecordDTO> alertRecordDTOS = pageRes.getList().stream().map(alertRecordAssembler::toDTO).collect(Collectors.toList());

        return Result.success(PageResult.of(pageRes.getCount(),alertRecordDTOS));
    }

    @Override
    public Result<Long> countAlertRecords(AlertRecordsCountQuery query) {
        return Result.success(alertRecordRepo.countAlertRecords(query.getTotal(),query.getStatus())) ;
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
                    List<String> codesParams = new ArrayList<>();

                    codesParams.add(userDTO.getPersonCode());

                    PersonPageQueryRequest request = PersonPageQueryRequest.builder()
                            .companyCode(CompanyEnum.DEFAULT.value)
                            .codes(codesParams)
                            .username(userDTO.getUsername())
                            .hasBoundUser(true)
                            .build();
                    Result<List<SuposPersonDTO>> peronFromSupos = suposPersonService.searchPeronFromSupos(request);

                    if (!peronFromSupos.isSuccess() || peronFromSupos.getData() == null || peronFromSupos.getData().isEmpty()) {
                        log.error("fetch person error: {}", JSON.toJSONString(userDTO));
                    }

                    for (SuposPersonDTO personDTO : peronFromSupos.getData()) {
                        SavePersonCommand savePersonCommand = new SavePersonCommand(personDTO);
                        Result savePerson = suposPersonService.savePerson(savePersonCommand);
                        if (!savePerson.isSuccess()) {
                            log.error("save person error: {}", savePerson.getMessage());
                        }
                    }

                }
            }

            personEO = suposPersonRepo.findByCode(userDTO.getPersonCode());
        }

        Preconditions.checkArgument(personEO != null, "personEO is null " + userDTO.getUsername());
        PERSON_CACHE.put(userDTO.getPersonCode(), personEO);
        phoneNumber = personEO.getPhone() == null ? null : personEO.getPhone().trim();
        email =  personEO.getEmail() == null ? null : personEO.getEmail().trim();


        List<SuposUserRoleDTO> userRoleListDTO = userDTO.getUserRoleList();

        List<UserRoleEO> userRoleEOList = new ArrayList<>();

        for (SuposUserRoleDTO userRoleDTO : userRoleListDTO) {
            UserRoleEO userRoleEO = UserRoleEO.builder()
                    .name(userRoleDTO.getName())
                    .showName(userRoleDTO.getShowName())
                    .description(userRoleDTO.getDescription())
                    .total(userRoleDTO.getTotal())
                    .build();
            userRoleEOList.add(userRoleEO);
        }

        UserEO userEO = UserEO.builder()
                .accountType(userDTO.getAccountType())
                .avatar(userDTO.getAvatar())
                .createTime(userDTO.getCreateTime())
                .lockStatus(userDTO.getLockStatus())
                .modifyTime(userDTO.getModifyTime())
                .personCode(userDTO.getPersonCode())
                .personName(userDTO.getPersonName())
                .userDesc(userDTO.getUserDesc())
                .username(userDTO.getUsername())
                .valid(userDTO.getValid())
                .userRoleList(userRoleEOList)
                .build();

        // 验证手机号
        if ( StringUtils.isNotBlank(phoneNumber) && isValidPhoneNumber(Objects.requireNonNull(phoneNumber))) {
            alertRecords.add(buildAlertRecordEO(alertEO.getAlertId(), userEO,userDTO.getUsername(), phoneNumber, null, MessageType.SMS, text, false,alarmEO));
        }

        // 验证邮箱
        if (StringUtils.isNotBlank(email) && isValidEmail(email)) {
            alertRecords.add(buildAlertRecordEO(alertEO.getAlertId(), userEO,userDTO.getUsername(), null, email, MessageType.EMAIL, text, false,alarmEO));
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

                    List<String> codesParam = new ArrayList<>();
                    codesParam.add(personEO.getCode());
                    PersonPageQueryRequest request = PersonPageQueryRequest.builder()
                            .companyCode(CompanyEnum.DEFAULT.value)
                            .codes(codesParam)
                            .username(userDTO.getUsername())
                            .build();
                    Result<List<SuposPersonDTO>> peronFromSupos = suposPersonService.searchPeronFromSupos(request);

                    if (!peronFromSupos.isSuccess() || peronFromSupos.getData() == null || peronFromSupos.getData().isEmpty()) {
                        log.error("获取用户信息失败: {}", userDTO.getPersonCode());
                    }

                    List<SuposPersonDTO> personDTOS = peronFromSupos.getData();

                    for (SuposPersonDTO personDTO : personDTOS) {
                        SavePersonCommand savePersonCommand = new SavePersonCommand(personDTO);
                        Result savePersonRes = suposPersonService.savePerson(savePersonCommand);

                        if (!savePersonRes.isSuccess()) {
                            log.error("save {} person error: {}", personDTO.getCode(),savePersonRes.getMessage());
                        }
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



    private AlertRecordEO buildAlertRecordEO(Long alertId, UserEO userEO, String username, String phone, String email, MessageType type, String text, Boolean status, AlarmEO alarmEO) {
        if (type == MessageType.SMS) {
            return AlertRecordEO.builder()
                    .type(MessageType.SMS)
                    .alertId(alertId)
                    .username(username)
                    .user(userEO)
                    .content(text)
                    .sendTime(new Date())
                    .phone(phone)
                    .status(status)
                    .alarm(alarmEO)
                    .alarmId(Long.valueOf(alarmEO.getAlarmId()))
                    .expire(false)
                    .build();
        }else {
            return AlertRecordEO.builder()
                    .type(MessageType.EMAIL)
                    .alertId(alertId)
                    .username(username)
                    .user(userEO)
                    .content(text)
                    .sendTime(new Date())
                    .email(email)
                    .status(status)
                    .alarm(alarmEO)
                    .alarmId(alarmEO.getId())
                    .expire(false)
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
