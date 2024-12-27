package org.niiish32x.sugarsms.alarm;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * AlarmDO
 *
 * @author shenghao ni
 * @date 2024.12.27 14:18
 */

@Data
@TableName("alarm")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableField
    private Long id;

    @TableField(value = "alarm_id")
    private String alarmId;

    @TableField(value = "display_name")
    private String displayName;

    @TableField(value = "priority")
    private Integer priority;

    @TableField(value = "enable")
    private Boolean enable;

    @TableField(value = "operator")
    private String operator;

    @TableField(value = "limit_value")
    private String limitValue;

    @TableField(value = "dead_band")
    private Double deadBand;

    @TableField(value = "dead_band_type")
    private String deadBandType;

    @TableField(value = "comment")
    private String comment;

    @TableField(value = "alarm_type")
    private String alarmType;

    @TableField(value = "en_name")
    private String enName;

    @TableField(value = "template_en_name")
    private String templateEnName;

    @TableField(value = "template_display_name")
    private String templateDisplayName;

    @TableField(value = "instance_en_name")
    private String instanceEnName;

    @TableField(value = "instance_display_name")
    private String instanceDisplayName;

    @TableField(value = "attribute_en_name")
    private String attributeEnName;

    @TableField(value = "attribute_display_name")
    private String attributeDisplayName;

    @TableField(value = "attribute_comment")
    private String attributeComment;

    @TableField(value = "instance_labels")
    private String instanceLabels;

    @TableField(value = "attribute_labels")
    private String attributeLabels;

    /**
     * 软删除标志位  0未删除 1表示删除
     */
    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;
}
