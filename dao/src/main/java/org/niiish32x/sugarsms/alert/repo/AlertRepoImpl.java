package org.niiish32x.sugarsms.alert.repo;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.niiish32x.sugarsms.alert.AlertDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertEO;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRepo;
import org.niiish32x.sugarsms.alert.persistence.converter.AlertConverter;
import org.niiish32x.sugarsms.alert.persistence.dao.AlertDAO;
import org.niiish32x.sugarsms.alert.persistence.dao.AlertRecordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlertRepoImpl
 *
 * @author shenghao ni
 * @date 2025.01.17 10:27
 */

@Repository
public class AlertRepoImpl implements AlertRepo {
    AlertConverter converter = AlertConverter.INSTANCE;

    @Autowired
    private AlertDAO alertDAO;

    @Override
    public boolean saveOrUpdate(AlertEO alertEO) {
        AlertDO alertDO = converter.toDO(alertEO);

        AlertDO existAlertDO = alertDAO.lambdaQuery().eq(AlertDO::getAlertId, alertDO.getAlertId()).one();

        if (existAlertDO != null) {
            LambdaUpdateWrapper<AlertDO> wrapper = Wrappers.<AlertDO>lambdaUpdate()
                    .eq(AlertDO::getId, existAlertDO.getId())
                    .set(AlertDO::getAlertId, alertDO.getAlertId())
                    .set(AlertDO::getAlertName, alertDO.getAlertName())
                    .set(AlertDO::getShowName, alertDO.getShowName())
                    .set(AlertDO::getPriority, alertDO.getPriority())
                    .set(AlertDO::getSource, alertDO.getSource())
                    .set(AlertDO::getSourceShowName, alertDO.getSourceShowName())
                    .set(AlertDO::getSourcePropertyName, alertDO.getSourcePropertyName())
                    .set(AlertDO::getSourcePropShowName, alertDO.getSourcePropShowName())
                    .set(AlertDO::getDescription, alertDO.getDescription())
                    .set(AlertDO::getNewValue, alertDO.getNewValue())
                    .set(AlertDO::getValType, alertDO.getValType())
                    .set(AlertDO::getOldValue, alertDO.getOldValue())
                    .set(AlertDO::getFinishGenerateAlertRecord, alertDO.getFinishGenerateAlertRecord());
            return alertDAO.update(existAlertDO,wrapper);
        }

        return alertDAO.save(alertDO);
    }

    @Override
    public List<AlertEO> findUnFinishedAlerts(int nums) {

        List<AlertDO> list = alertDAO.lambdaQuery().eq(AlertDO::getFinishGenerateAlertRecord, 0).list();

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        list = list.subList(0, nums);

        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }
}
