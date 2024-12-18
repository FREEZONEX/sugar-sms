package org.niiish32x.sugarsms.alert.domain.repo;

import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;

import java.sql.Time;
import java.util.List;

/**
 * AlertRecordRepo
 *
 * @author shenghao ni
 * @date 2024.12.16 18:25
 */

public interface AlertRecordRepo {
    List<AlertRecordEO>  find();

    /**
     * 根据消息Id 进行查找
     * @param alertId
     * @return
     */
    AlertRecordEO find(Long alertId);

    boolean save(AlertRecordEO alertRecordEO);

    /**
     * 找到所有发送失败的记录
     * @return
     */
    List<AlertRecordEO> findFailRecords();

    /**
     * 找到 alert消息 第一条发送 和 最后一条发送的时间间隔
     * @return
     */
    Long findDurationFromStatToEnd(long alertId);

    Long findDurationFromStatToEnd(long alertId, MessageType type);

    /**
     * 测试使用
     * 找当前库 发送时间最早的消息 到 发送时间最迟的消息之间的间隔
     * @return
     */
    Long findDurationFromStatToEnd(MessageType type);
}
