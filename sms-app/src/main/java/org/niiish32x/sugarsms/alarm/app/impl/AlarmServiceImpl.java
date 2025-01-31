package org.niiish32x.sugarsms.alarm.app.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alarm.app.assembler.AlarmAssembler;
import org.niiish32x.sugarsms.alarm.app.command.SaveAlarmCommand;
import org.niiish32x.sugarsms.alarm.app.external.AlarmRequest;
import org.niiish32x.sugarsms.alarm.app.query.AlarmQuery;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.repo.AlarmRepo;
import org.niiish32x.sugarsms.alarm.persistence.converter.AlarmConverter;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmDTO;
import org.niiish32x.sugarsms.api.alarm.dto.AlarmPageResponse;
import org.niiish32x.sugarsms.common.enums.ApiEnum;
import org.niiish32x.sugarsms.alarm.app.AlarmService;
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
            queryMap = request.buildQueryMap();

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
    public Result<Boolean> save(SaveAlarmCommand command) {

        AlarmDTO alarmDTO = command.getAlarmDTO();
        AlarmEO alarmEO = alarmAssembler.alarmDTO2EO(alarmDTO);
        boolean res = alarmRepo.saveOrUpdate(alarmEO);
        return res ? Result.success(true) : Result.error("保存失败");
    }

    @Override
    public Result<AlarmDTO> getAlarm(AlarmQuery query) {
        AlarmEO alarmEO = alarmRepo.findWithAttributeEnName(query.getAttributeEnName());
        AlarmDTO alarmDTO = alarmAssembler.alarmEO2DTO(alarmEO);
        return Result.success(alarmDTO);
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

            SaveAlarmCommand command = new SaveAlarmCommand(alarmDTO);
            Result<Boolean> res = save(command);

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
