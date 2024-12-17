package org.niiish32x.sugarsms.alert.repo;

import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.alert.persistence.converter.AlertRecordConverter;
import org.niiish32x.sugarsms.alert.persistence.dao.AlertRecordDAO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlertRecordRepoImpl
 *
 * @author shenghao ni
 * @date 2024.12.16 18:25
 */


@Repository
public class AlertRecordRepoImpl implements AlertRecordRepo {


    AlertRecordConverter converter = AlertRecordConverter.INSTANCE;

    @Resource
    private AlertRecordDAO alertRecordDAO;


    @Override
    public List<AlertRecordEO> find() {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery().list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public boolean save(AlertRecordEO alertRecordEO) {

        return alertRecordDAO.save(converter.toDO(alertRecordEO));
    }

    @Override
    public List<AlertRecordEO> findFailRecords() {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getStatus, false).list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }
}
