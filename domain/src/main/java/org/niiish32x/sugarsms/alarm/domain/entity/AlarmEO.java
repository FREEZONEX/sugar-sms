package org.niiish32x.sugarsms.alarm.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * AlarmEO
 *
 * @author shenghao ni
 * @date 2024.12.27 14:35
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String alarmId;
    private String displayName;
    private Integer priority;
    private Boolean enable;
    private String operator;
    private String limitValue;
    private Double deadBand;
    private String deadBandType;
    private String comment;
    private String alarmType;
    private String enName;
    private String templateEnName;
    private String templateDisplayName;
    private String instanceEnName;
    private String instanceDisplayName;
    private String attributeEnName;
    private String attributeDisplayName;
    private String attributeComment;
    private String instanceLabels;
    private String attributeLabels;
    private Boolean deleted;
    private Date createTime;
    private Date updateTime;

    // 以下可以添加一些业务相关的方法示例，比如根据某个条件判断是否有效等逻辑

    /**
     * 判断报警是否处于启用状态
     *
     * @return true 如果启用，false 则未启用
     */
    public boolean isAlarmEnabled() {
        return enable!= null && enable;
    }

    /**
     * 判断是否已经被软删除
     *
     * @return true 如果已删除，false 则未删除
     */
    public boolean isDeleted() {
        return deleted;
    }
}























