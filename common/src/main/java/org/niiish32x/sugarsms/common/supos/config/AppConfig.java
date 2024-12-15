package org.niiish32x.sugarsms.common.supos.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * SdkConfig
 *
 * @author shenghao ni
 * @date 2024.12.08 13:58
 */

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppConfig {
    /**
     * supOS地址
     */
//    @Value("${supos.supos-address}")
    private String suposWebAddress = "http://192.168.2.171:8080";

    /**
     * supOS appId
     * ak
     */
    @Value("${supos.app-id}")
    private String appId;

    /**
     * supOS appSecret
     * sk
     */
    @Value("${supos.app-secret}")
    private String appSecret;

    @Value("${supos.ak}")
    private String ak;

    @Value("${supos.sk}")
    private String sk;

}