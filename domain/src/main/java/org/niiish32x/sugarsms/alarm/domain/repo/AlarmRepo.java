package org.niiish32x.sugarsms.alarm.domain.repo;

import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;

import java.util.List;

/**
 * AlarmRepo
 *
 * @author shenghao ni
 * @date 2024.12.27 14:41
 */
public interface AlarmRepo {

    List<AlarmEO> find();

    boolean save(AlarmEO alarmEO);
}
