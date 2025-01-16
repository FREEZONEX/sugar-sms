package org.niiish32x.sugarsms.alert.domain.repo;

import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;

import java.util.List;

/**
 * AlertRecordRepo
 *
 * @author shenghao ni
 * @date 2024.12.16 18:25
 */

public interface AlertRecordRepo {
    List<Long> findPendingSendEmailAlertIds();

    List<Long> findPendingSendSmsAlertIds();

    List<Long> findByAlertIdsByStatus(boolean status);

    List<AlertRecordEO> findAlertsBeforeDays(Integer days);

    List<AlertRecordEO>  find();

    /**
     * 根据消息Id 进行查找
     * @param alertId
     * @return
     */
    List<AlertRecordEO> findByAlertId(Long alertId);

    AlertRecordEO findById(Long id);

    List<Long> findExistingAlertIds(List<Long> alertIds);

    /**
     * 找到所有发送失败记录
     * @return
     */
    List<AlertRecordEO> findByAlertId(MessageType type, boolean status);

    boolean save(AlertRecordEO alertRecordEO);



    boolean update(AlertRecordEO alertRecordEO);

    boolean save(List<AlertRecordEO> alertRecordEOS);

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

    /**
     * 根据id 批量删除记录
     * @param ids
     * @return
     */
    Boolean remove(List <Long> ids);

    AlertRecordEO findWithLimitByAlertId(Long alertId,Integer limit);

    boolean updateStatusById(Long alertId, boolean status);
}
