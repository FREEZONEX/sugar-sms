package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.niiish32x.sugarsms.alert.AlertDO;

import java.util.List;

/**
 * AlertDAO
 *
 * @author shenghao ni
 * @date 2025.01.17 10:24
 */
public interface AlertDAO extends IService<AlertDO> {
    List<AlertDO> findUnFinishedAlertsWithLimits(@Param("limit") int limit);
}
