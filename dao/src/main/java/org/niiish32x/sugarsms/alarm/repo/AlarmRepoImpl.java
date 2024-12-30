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
    public List<AlarmEO> list() {
        List<AlarmDO> list = alarmDAO.lambdaQuery().list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public AlarmEO findWithAttributeEnName(String attributeEnName) {
        List<AlarmDO> alarmDOS = alarmDAO.lambdaQuery().eq(AlarmDO::getAttributeEnName, attributeEnName).list();
        return !alarmDOS.isEmpty() ? converter.toEO(alarmDOS.get(0)) : null;
    }


    @Override
    public boolean saveOrUpdate(AlarmEO alarmEO) {
        AlarmDO alarmDO = converter.toDO(alarmEO);
        // 查询是否存在相同 alarmId 的记录
        AlarmDO existingAlarm = alarmDAO.lambdaQuery().eq(AlarmDO::getAlarmId, alarmDO.getAlarmId()).one();

        if (existingAlarm != null) {
            // 更新现有记录
            alarmDO.setId(existingAlarm.getId()); // 确保主键一致
            return alarmDAO.updateById(alarmDO);
        } else {
            // 插入新记录
            return alarmDAO.save(alarmDO);
        }
    }

    @Override
    public AlarmEO find(String alarmId) {
        List<AlarmDO> list = alarmDAO.lambdaQuery().eq(AlarmDO::getAlarmId, alarmId).list();
        return list.get(0) != null ? converter.toEO(list.get(0)) : null;
    }
}
