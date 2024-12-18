package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.ibatis.annotations.Param;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
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
    public List<AlertRecordDO> findAlertBeforeDays(@Param("days") Integer days){
        return getBaseMapper().findAlertBeforeDays(days);
    }
}
