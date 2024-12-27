
use sugar_sms;

drop table alert_record;

CREATE TABLE `alert_record` (   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                `alert_id` bigint(20) NOT NULL ,
                                `type` VARCHAR(255) COMMENT '通知类型',
                                `status` tinyint(1)   COMMENT '0 表示 sms 发送未完成 1 表示 sms 发送已完成 即全部通知到位' ,
                                `username` VARCHAR(255),
                                `send_time` datetime COMMENT  '通知成功的时间',
                                `phone` VARCHAR(255),
                                `email` VARCHAR(255),
                                `content` TEXT COMMENT '通知的具体内容' ,
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                index idx_status(status),
                                index idx_alert_id(alert_id),
                                index idx_type(type),
                                unique idx_uni_alert_id_type_username(alert_id,type,username),
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警消息记录表';

drop table alarm;


use sugar_sms;
drop table alarm;
CREATE TABLE `alarm` (
                         `id`  bigint(20)  NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
                         `alarm_id`  VARCHAR(255)  DEFAULT  NULL COMMENT '报警消息的Id',
                         `display_name` VARCHAR(255) DEFAULT NULL,
                         `priority` INT NOT NULL,
                         `enable` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '0表示false，1表示true',
                         `operator` VARCHAR(50) DEFAULT NULL,
                         `limit_value` VARCHAR(255) DEFAULT NULL,
                         `dead_band` DECIMAL(10, 2) DEFAULT NULL,  -- 根据double类型，设置合适的精度和小数位数
                         `dead_band_type` VARCHAR(50) DEFAULT NULL,
                         `comment` VARCHAR(255) DEFAULT NULL,
                         `alarm_type` VARCHAR(50) DEFAULT NULL,
                         `en_name` VARCHAR(255) DEFAULT NULL,
                         `template_en_name` VARCHAR(255) DEFAULT NULL,
                         `template_display_name` VARCHAR(255) DEFAULT NULL,
                         `instance_en_name` VARCHAR(255) DEFAULT NULL,
                         `instance_display_name` VARCHAR(255) DEFAULT NULL,
                         `attribute_en_name` VARCHAR(255) DEFAULT NULL,
                         `attribute_display_name` VARCHAR(255) DEFAULT NULL,
                         `attribute_comment` VARCHAR(255) DEFAULT NULL,
                         `instance_labels` TEXT DEFAULT NULL,
                         `attribute_labels` TEXT DEFAULT NULL,
                         `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除状态 0表示未删除 1表示删除 用于软删除',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                         index idx_deleted(deleted),
                         index idx_limit_value(limit_value),
                         unique idx_uni_alarm_id(alarm_id),
                         index idx_attribute_en_name(attribute_en_name)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警信息表';

