package org.niiish32x.sugarsms.alert.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;

import java.util.List;

/**
 * AlertRecordMapper
 *
 * @author shenghao ni
 * @date 2024.12.16 18:23
 */
public interface AlertRecordMapper extends BaseMapper<AlertRecordDO> {

    /**
     * 获取距离当前时间 days 所有 alert数据
     * @return
     */
    @Select("select * from sugar_sms.alert_record where TIMESTAMPDIFF(DAY, send_time, CURRENT_TIMESTAMP) <= #{days}")
    List<AlertRecordDO> findAlertBeforeDays(@Param("days") Integer days);

    /**
     * limit查找  判断的时候用减少全表扫描
     * @param alertId
     * @param limit
     * @return
     */
    @Select("select * from sugar_sms.alert_record where alert_id=#{alertId} limit #{limit}")
    AlertRecordDO findWithLimitByAlertId(@Param("alertId") Long alertId,@Param("limit") Integer limit);
}
