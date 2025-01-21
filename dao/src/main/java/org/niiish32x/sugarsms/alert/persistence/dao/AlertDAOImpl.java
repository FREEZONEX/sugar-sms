package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.niiish32x.sugarsms.alert.AlertDO;
import org.niiish32x.sugarsms.alert.persistence.mapper.AlertMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AlertDAOImpl
 *
 * @author shenghao ni
 * @date 2025.01.17 10:24
 */
@Component
public class AlertDAOImpl extends ServiceImpl<AlertMapper, AlertDO> implements AlertDAO {
    @Override
    public List<AlertDO> findUnFinishedAlertsWithLimits(int limit) {
        return getBaseMapper().findUnFinishedAlertsWithLimits(limit);
    }
}
