package org.niiish32x.sugarsms.alert.domain.repo;

import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;

import java.util.List;

/**
 * AlertRecordRepo
 *
 * @author shenghao ni
 * @date 2024.12.16 18:25
 */

public interface AlertRecordRepo {
    List<AlertRecordEO>  find();

    boolean save(AlertRecordEO alertRecordEO);

    /**
     * 找到所有发送失败的记录
     * @return
     */
    List<AlertRecordEO> findFailRecords();
}
