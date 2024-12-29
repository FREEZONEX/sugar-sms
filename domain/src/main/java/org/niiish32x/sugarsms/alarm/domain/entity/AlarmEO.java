package org.niiish32x.sugarsms.alarm.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
//    private String instanceLabels;
    private List<InstanceLabelEO> instanceLabels;
//    private String attributeLabels;
    private List<AttributeLabelEO>  attributeLabels;
    private boolean deleted;
    private Date createTime;
    private Date updateTime;
}























