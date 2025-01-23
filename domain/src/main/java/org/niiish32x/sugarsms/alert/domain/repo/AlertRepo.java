package org.niiish32x.sugarsms.alert.domain.repo;

import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;

import java.util.List;

/**
 * AlertRepo
 *
 * @author shenghao ni
 * @date 2025.01.17 10:27
 */
public interface AlertRepo {
    boolean saveOrUpdate(AlertEO alertEO);

    List<AlertEO> find(AlertEO alertEO);

    List<AlertEO> find();

    AlertEO findByAlertId(Long alertId);


    List<AlertEO> findUnFinishedAlerts(int nums);

    List<AlertEO> findUnFinishedAlerts();
}
