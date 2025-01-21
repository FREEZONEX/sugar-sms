package org.niiish32x.sugarsms.alert.persistence.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.niiish32x.sugarsms.alert.AlertRecordDO;

import java.util.List;

/**
 * AlertRecordDAO
 *
 * @author shenghao ni
 * @date 2024.12.16 18:22
 */
public interface AlertRecordDAO extends IService<AlertRecordDO> {
    List<AlertRecordDO> findAlertBeforeDays(@Param("days") Integer days);

    List<Long> findByAlertIdsByStatus(@Param("status") boolean status);

    List<Long> findAlertIdsByTypeAndStatus(@Param("type") String type, @Param("status") boolean status,@Param("limit") int limit);

    boolean updateStatusById(@Param("id") Long id, @Param("status") boolean status);

    boolean updateExpireById(@Param("id") Long id, @Param("expire") boolean expire);
}
