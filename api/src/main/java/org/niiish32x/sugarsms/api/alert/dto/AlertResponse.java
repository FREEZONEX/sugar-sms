package org.niiish32x.sugarsms.api.alert.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.List;

/**
 * AlertResponse
 *
 * @author shenghao ni
 * @date 2024.12.11 15:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertResponse implements Serializable {
    private Integer code;
    private String message;
    @JSONField(name = "data")
    private List<AlertInfoDTO> alerts;
    private String detail;
    private String context;
    private String targetService;
}
