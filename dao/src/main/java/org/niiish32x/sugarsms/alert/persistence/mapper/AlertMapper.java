package org.niiish32x.sugarsms.alert.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.niiish32x.sugarsms.alert.AlertDO;

import java.util.List;

/**
 * AlertMapper
 *
 * @author shenghao ni
 * @date 2025.01.17 10:26
 */
public interface AlertMapper extends BaseMapper<AlertDO> {

    @Select("select * from sugar_sms.alert where finish_generate_alert_record = 0 limit #{limit}")
    List<AlertDO> findUnFinishedAlertsWithLimits(@Param("limit") int limit);
}
