
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

drop table alert_fail_record;

CREATE TABLE `alert_fail_record` (
                                `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
                                `alert_id` bigint(20) NOT NULL ,
                                `type` VARCHAR(255) COMMENT '通知的方式 sms 或者 email',
                                `user_name` VARCHAR(255) ,
                                `user_email` VARCHAR(255),
                                `user_phone` VARCHAR(255),
                                `status` tinyint(1) COMMENT '0 表示仍然通知失败 1 表示已经重试成功',
                                `content` TEXT COMMENT '通知的具体内容' ,
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                index idx_status(status),
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报警通知失败记录表';