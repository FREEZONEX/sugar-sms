package org.niiish32x.sugarsms.app.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsRequest;
import org.niiish32x.sugarsms.common.utils.TimeUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * ZubrixSmsProxy
 *
 * 实现对 zubrixtechnologies 服务商服务逻辑的代理
 *
 * @author shenghao ni
 * @date 2024.12.11 10:29
 */

@Component
@Slf4j
public class ZubrixSmsProxy {

    private final String zubrixSmsBaseUrl = "http://cloudsms.zubrixtechnologies.com/api/mt/SendSMS";

    private final String SMS_TEXT_TEMPLATE = "KPI %s exceeded to %s Threshold/Limit value %s value Location %s Date/Time%s Dhampur Sugar Mills";

    public ZubrixSmsResponse send(String number,String text) {
        ZubrixSmsRequest request = buildRequest(number, text);
        String url = buildUrl(request);
        return send(url);
    }

    public ZubrixSmsResponse send(String url) {
//        log.info("开始发送短信");
//        log.info("Zubrix url: {}",url);
        HttpResponse response = HttpRequest.get(url).execute();
        ZubrixSmsResponse messageResponse = JSON.parseObject(response.body(), ZubrixSmsResponse.class);
        return messageResponse;
    }


    public String formatTextContent(AlertInfoDTO alertInfoDTO) {
        String time = TimeUtil.formatTimeStamp(alertInfoDTO.getStartDataTimestamp());
        String text = String.format(SMS_TEXT_TEMPLATE, alertInfoDTO.getSourcePropertyName(), alertInfoDTO.getNewValue(), "0", alertInfoDTO.getSource(), time);
        return text;
    }

    public String formatTextContent(AlertContentBuilder alertContentBuilder) {
        String sourcePropertyName = alertContentBuilder.getSourcePropertyName() != null ? alertContentBuilder.getSourcePropertyName() : "未知属性";
        String newValue = alertContentBuilder.getNewValue() != null ? alertContentBuilder.getNewValue() : "未知值";
        String source = alertContentBuilder.getSource() != null ? alertContentBuilder.getSource() : "未知来源";
        Long startDataTimestamp = alertContentBuilder.getStartDataTimestamp();

        String time;
        try {
            time = startDataTimestamp != null ? TimeUtil.formatTimeStamp(startDataTimestamp) : "未知时间";
        } catch (Exception e) {
            // 记录日志或采取其他措施
            time = "时间格式化失败";
        }

        return String.format(SMS_TEXT_TEMPLATE, sourcePropertyName, newValue, alertContentBuilder.getLimitValue(), source, time);
    }

    public String formatTextContent(AlertInfoDTO alertInfoDTO, String limitValue) {
        if (alertInfoDTO == null) {
            throw new IllegalArgumentException("alertInfoDTO cannot be null");
        }

        String sourcePropertyName = alertInfoDTO.getSourcePropertyName() != null ? alertInfoDTO.getSourcePropertyName() : "未知属性";
        Object newValue = alertInfoDTO.getNewValue() != null ? alertInfoDTO.getNewValue() : "未知值";
        String source = alertInfoDTO.getSource() != null ? alertInfoDTO.getSource() : "未知来源";
        Long startDataTimestamp = alertInfoDTO.getStartDataTimestamp();

        String time;
        try {
            time = startDataTimestamp != null ? TimeUtil.formatTimeStamp(startDataTimestamp) : "未知时间";
        } catch (Exception e) {
            // 记录日志或采取其他措施
            time = "时间格式化失败";
        }

        return String.format(SMS_TEXT_TEMPLATE, sourcePropertyName, newValue, limitValue, source, time);
    }

     public ZubrixSmsRequest buildRequest(String number,String text) {
         return ZubrixSmsRequest.builder()
                 .user("SUPINCO123")
                 .password("123456")
                 .senderId("DSMDPR")
                 .channel("Trans")
                 .dcs("0")
                 .flashSMS("0")
                 .number(number)
                 .text(text)
                 .route("02")
                 .dltTemplateId("1607100000000331206")
                 .peid("1601100000000014322")
                 .build();
    }

    public String buildUrl(ZubrixSmsRequest request) {
        List<String> queryList = Arrays.asList(
                "user=" + request.getUser(),
                "password=" + request.getPassword(),
                "senderid=" + request.getSenderId(),
                "channel=" + request.getChannel(),
                "DCS=" + request.getDcs(),
                "flashsms=" + request.getFlashSMS(),
                "number=" +  request.getNumber(),
                "text=" + request.getText(),
                "route=" +  request.getRoute(),
                "DLTTemplateId=" + request.getDltTemplateId(),
                "PEID=" + request.getPeid()
        );

        String url = zubrixSmsBaseUrl + "?" + String.join("&",queryList);

        return url;
    }




//    public static void main(String[] args) {
//        ZubrixSmsProxy smsProxy = new ZubrixSmsProxy();
//        ZubrixSmsRequest request = smsProxy.buildRequest("123", "xx");
//        String url = smsProxy.buildUrl(request);
//        System.out.println(url);
//
//    }
}
