package org.niiish32x.sugarsms.alert.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.niiish32x.sugarsms.alert.AlertRecordDO;

import java.util.List;

/**
 * AlertRecordMapper
 *
 * @author shenghao ni
 * @date 2024.12.16 18:23
 */
public interface AlertRecordMapper extends BaseMapper<AlertRecordDO> {

    /**
     * 根据status 获取alertIds (主要是对于频繁查询语句进行优化)
     * 走覆盖索引减少 回表查询 \ 只查对应列 检索速度
     * @param status
     * @return
     *
     * seLect_type: simple
     * type: index
     * key: idx_status \ key_len:2
     * extra: using index
     */
    @Select("select id from alert_record where status = #{status} ")
    List<Long> findByAlertIdsByStatus(@Param("status") boolean status);

    @Select("select id from alert_record where type = #{type} and status = #{status} limit #{limit}")
    List<Long> findAlertIdsByTypeAndStatus(@Param("type") String type, @Param("status") boolean status,@Param("limit") int limit);

    /**
     * 获取距离当前时间 days 所有 alert数据
     * @return
     */
    @Select("select * from sugar_sms.alert_record where TIMESTAMPDIFF(DAY, send_time, CURRENT_TIMESTAMP) <= #{days}")
    List<AlertRecordDO> findAlertBeforeDays(@Param("days") Integer days);


    @Update("update alert_record set status = #{status} where id = #{id}")
    boolean updateStatusById(@Param("id") Long id, @Param("status") boolean status);

    @Update("update alert_record set expire = #{expire} where id = #{id}")
    boolean updateExpireById(@Param("id") Long id, @Param("expire") boolean expire);
}
