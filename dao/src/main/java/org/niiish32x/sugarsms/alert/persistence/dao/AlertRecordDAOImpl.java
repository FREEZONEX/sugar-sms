package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.persistence.mapper.AlertRecordMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AlertRecordDAOImpl
 *
 * @author shenghao ni
 * @date 2024.12.16 18:22
 */
@Component
public class AlertRecordDAOImpl extends ServiceImpl<AlertRecordMapper, AlertRecordDO> implements AlertRecordDAO{
    @Override
    public List<AlertRecordDO> findAlertBeforeDays(Integer days){
        return getBaseMapper().findAlertBeforeDays(days);
    }


    @Override
    public List<Long> findByAlertIdsByStatus(boolean status) {
        return getBaseMapper().findByAlertIdsByStatus(status);
    }

    @Override
    public List<Long> findAlertIdsByTypeAndStatus(String type, boolean status,int limit) {
        return getBaseMapper().findAlertIdsByTypeAndStatus(type,status,limit);
    }

    @Override
    public boolean updateStatusById(Long alertId, boolean status) {
        return getBaseMapper().updateStatusById(alertId,status);
    }
}
