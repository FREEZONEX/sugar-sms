package org.niiish32x.sugarsms.alarm.repo;

import org.niiish32x.sugarsms.alarm.AlarmDO;
import org.niiish32x.sugarsms.alarm.domain.entity.AlarmEO;
import org.niiish32x.sugarsms.alarm.domain.repo.AlarmRepo;
import org.niiish32x.sugarsms.alarm.persistence.converter.AlarmConverter;
import org.niiish32x.sugarsms.alarm.persistence.dao.AlarmDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AlarmRepoImpl
 *
 * @author shenghao ni
 * @date 2024.12.27 14:46
 */


@Repository
public class AlarmRepoImpl implements AlarmRepo {

    AlarmConverter converter = AlarmConverter.INSTANCE;

    @Autowired
    private AlarmDAO alarmDAO;


    @Override
    public List<AlarmEO> find() {
        List<AlarmDO> list = alarmDAO.lambdaQuery().list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public boolean save(AlarmEO alarmEO) {
        AlarmDO alarmDO = converter.toDO(alarmEO);
        return alarmDAO.save(alarmDO);
    }
}
