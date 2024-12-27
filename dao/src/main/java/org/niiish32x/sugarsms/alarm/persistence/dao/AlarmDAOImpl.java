package org.niiish32x.sugarsms.alarm.persistence.dao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.niiish32x.sugarsms.alarm.AlarmDO;
import org.niiish32x.sugarsms.alarm.persistence.mapper.AlarmMapper;
import org.springframework.stereotype.Component;

/**
 * AlarmDAOImpl
 *
 * @author shenghao ni
 * @date 2024.12.27 14:31
 */
@Component
public class AlarmDAOImpl extends ServiceImpl<AlarmMapper, AlarmDO> implements AlarmDAO{
}
