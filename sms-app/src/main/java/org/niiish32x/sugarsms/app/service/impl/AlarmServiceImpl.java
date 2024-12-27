package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.dto.AlarmDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.external.AlarmPageResponse;
import org.niiish32x.sugarsms.app.service.AlarmService;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlarmServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.27 15:02
 */

@Service
@Slf4j
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    SuposRequestManager requestManager;

    @Override
    public Result<List<AlarmDTO>> getAlarmsFromSupos(String attributeEnName) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("attributeEnName",attributeEnName);

        try {
            HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_SPEC_API.value, headerMap, queryMap);

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
    public Result<List<AlarmDTO>> getAlarmsFromSupos() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();

        try {
            HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_SPEC_API.value, headerMap, queryMap);

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
}
