package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import com.bluetron.eco.sdk.api.SuposRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AlertServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.10 10:03
 */

@Service
@Slf4j
public class AlertServiceImpl implements AlertService {

    @Resource
    SuposRequestManager requestManager;

    @Data
    class AlertResponse implements Serializable {
        private Integer code;
        private String message;
        @JSONField(name = "data")
        private List<AlertInfoDTO> alerts;
        private String detail;
        private String context;
        private String targetService;
    }


    @Override
    public Result getAlert() {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        HttpResponse response = requestManager.suposApiGet(ApiEnum.ALERT_API.value, headerMap, queryMap);
        AlertResponse alertResponse = JSON.parseObject(response.body(), AlertResponse.class);
        return alertResponse.getCode() == 200 ? Result.build(alertResponse.getAlerts(), ResultCodeEnum.SUCCESS) : Result.build(alertResponse,ResultCodeEnum.FAIL) ;
    }
}
