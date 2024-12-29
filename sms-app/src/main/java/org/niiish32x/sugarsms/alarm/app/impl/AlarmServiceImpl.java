package org.niiish32x.sugarsms.alarm.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alarm.app.assembler.AlarmAssembler;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.repo.AlarmRepo;
import org.niiish32x.sugarsms.alarm.persistence.converter.AlarmConverter;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmPageResponse;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlarmServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.29 10:11
 */
@Service
@Slf4j
public class AlarmServiceImpl implements AlarmService {
    @Resource
    SuposRequestManager requestManager;

    @Autowired
    AlarmRepo alarmRepo;


    AlarmConverter alarmConverter = AlarmConverter.INSTANCE;

    AlarmAssembler alarmAssembler = AlarmAssembler.INSTANCE;

    @Override
    public Result<List<AlarmDTO>> getAlarmsFromSupos(AlarmRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        try {

            if(StringUtils.isNotBlank(request.getInstanceEnName())){
                queryMap.put("instanceEnName",request.getInstanceEnName());
            }

            if (StringUtils.isNotBlank(request.getInstanceDisplayName())) {
                queryMap.put("instanceDisplayName",request.getAlarmDisplayName());
            }

            if (StringUtils.isNotBlank(request.getAttributeEnName())) {
                queryMap.put("attributeEnName",request.getAttributeEnName());
            }

            if (StringUtils.isNotBlank(request.getAttributeComment())) {
                queryMap.put("attributeComment",request.getAttributeComment());
            }

            if (StringUtils.isNotBlank(request.getAlarmEnName())) {
                queryMap.put("alarmEnName",request.getAlarmEnName());
            }

            if (StringUtils.isNotBlank(request.getAlarmDisplayName())){
                queryMap.put("alarmDisplayName",request.getAlarmDisplayName());
            }

            if (StringUtils.isNotBlank(request.getAlarmComment())){
                queryMap.put("alarmComment",request.getAlarmComment());
            }

            if (StringUtils.isNotBlank(request.getAlarmType())){
                queryMap.put("alarmType",request.getAlarmType());
            }

            if (request.getAlarmPriority() != null && request.getAlarmPriority() > 0 && request.getAlarmPriority() <= 10){
                queryMap.put("alarmPriority",request.getAlarmPriority().toString());
            }

            if (request.getAlarmPriority() != null &&  request.getPage() > 0) {
                queryMap.put("page",request.getPage().toString());
            }

            if (request.getPerPage() != null &&  request.getPerPage() > 0 && request.getPerPage() <= 500) {
                queryMap.put("perPage",request.getPerPage().toString());
            }

            HttpResponse response = requestManager.suposApiGet(ApiEnum.ALARM_API.value, headerMap, queryMap);

            if (!response.isOk()) {
                log.error("请求失败，状态码: {}", response.getStatus());
                return Result.error("请求异常");
            }

            if (response.body() == null || response.body().trim().isEmpty()) {
                log.error("响应体为空");
                return Result.error("响应体为空");
            }


            AlarmPageResponse alarmPageResponse = JSON.parseObject(response.body(), AlarmPageResponse.class);

            if (alarmPageResponse == null) {
                log.error("解析响应体失败");
                return Result.error("解析响应体失败");
            }


            List<AlarmDTO> alertList = alarmPageResponse.getList();

            return Result.success(alertList);

        } catch (Exception e) {
            log.error("请求过程中发生异常", e);
            return Result.error("请求过程中发生异常");
        }
    }

    @Override
    public Result<Boolean> save(AlarmDTO alarmDTO) {
        // 输入参数校验
        if (alarmDTO == null) {
            log.warn("Input parameter 'alarmDTO' is null");
            return Result.error("输入参数为空");
        }

        try {
            // DTO 转换为 EO
            AlarmEO alarmEO = alarmAssembler.alarmDTO2EO(alarmDTO);

            // 保存操作
            boolean res = alarmRepo.save(alarmEO);

            // 返回结果
            return res ? Result.success(true) : Result.error("保存失败");
        } catch (Exception e) {
            // 异常处理与日志记录
            log.error("保存报警信息失败: {}", e.getMessage(), e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }

    @Override
    public Result<Boolean> syncAlarmFromSupos() {
        // 获取报警信息
        Result<List<AlarmDTO>> alarmsFromSupos = getAlarmsFromSupos(new AlarmRequest());

        // 检查获取结果是否成功
        if (!alarmsFromSupos.isSuccess()) {
            return Result.error("Failed to fetch alarms from Supos: " + alarmsFromSupos.getMessage());
        }

        List<AlarmDTO> alarmDTOS = alarmsFromSupos.getData();

        // 检查数据是否为空
        if (alarmDTOS == null || alarmDTOS.isEmpty()) {
            return Result.success(true); // 或者根据业务逻辑返回适当的值
        }

        boolean allSavedSuccessfully = true;
        List<String> failedAlarms = new ArrayList<>();

        for (AlarmDTO alarmDTO : alarmDTOS) {
            Result<Boolean> res = save(alarmDTO);

            // 检查保存结果
            if (!res.isSuccess()) {
                allSavedSuccessfully = false;
                failedAlarms.add("Failed to save alarm: " + alarmDTO.toString() + ", error: " + res.getMessage());
            }
        }

        if (allSavedSuccessfully) {
            return Result.success(true);
        } else {
            return Result.error("Some alarms failed to save: " + String.join(", ", failedAlarms));
        }
    }
}
