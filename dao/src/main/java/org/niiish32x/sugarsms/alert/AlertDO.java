package org.niiish32x.sugarsms.alert;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * AlertDO
 *
 * @author shenghao ni
 * @date 2025.01.17 10:15
 */

@Data
@TableName("alert")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDO implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    @TableField(value = "alert_id")
    private Long alertId;
    @TableField(value = "alert_name")
    private String alertName;
    @TableField(value = "show_name")
    private String showName;
    @TableField(value = "priority")
    private Integer priority;
    @TableField(value = "source")
    private String source;
    @TableField(value = "source_show_name")
    private String sourceShowName;
    @TableField(value = "source_property_name")
    private String sourcePropertyName;
    @TableField(value = "source_prop_show_name")
    private String sourcePropShowName;
    @TableField(value = "description")
    private String description;
    @TableField(value = "new_value")
    private String newValue;
    @TableField(value = "val_type")
    private Integer valType;
    @TableField(value = "old_value")
    private String oldValue;
    @TableField(value = "finish_generate_alert_record")
    private Integer finishGenerateAlertRecord;
    @TableField(value = "start_data_timestamp")
    private long startDataTimestamp;
}
