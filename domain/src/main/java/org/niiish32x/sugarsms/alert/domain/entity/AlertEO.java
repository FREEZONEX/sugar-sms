package org.niiish32x.sugarsms.alert.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AlertEO
 *
 * @author shenghao ni
 * @date 2025.01.17 10:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEO {
    private Long id;
    private Long alertId;
    private String alertName;
    private String showName;
    private Integer priority;
    private String source;
    private String sourceShowName;
    private String sourcePropertyName;
    private String sourcePropShowName;
    private String description;
    private String newValue;
    private Integer valType;
    private String oldValue;
    private boolean finishGenerateAlertRecord;
}
