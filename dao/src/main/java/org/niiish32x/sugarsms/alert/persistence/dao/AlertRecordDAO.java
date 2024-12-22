package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;

import java.util.List;

/**
 * AlertRecordDAO
 *
 * @author shenghao ni
 * @date 2024.12.16 18:22
 */
public interface AlertRecordDAO extends IService<AlertRecordDO> {
    List<AlertRecordDO> findAlertBeforeDays(@Param("days") Integer days);
    AlertRecordDO findWithLimitByAlertId(@Param("alertId") Long alertId,@Param("limit") Integer limit);
}
