package org.niiish32x.sugarsms.alert.repo;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.base.Preconditions;
import com.sun.prism.null3d.NULL3DPipeline;
import org.apache.commons.lang3.StringUtils;
import org.niiish32x.sugarsms.alert.AlertRecordDO;
import org.niiish32x.sugarsms.alert.domain.entity.AlertRecordEO;
import org.niiish32x.sugarsms.alert.domain.entity.MessageType;
import org.niiish32x.sugarsms.alert.domain.repo.AlertRecordRepo;
import org.niiish32x.sugarsms.alert.persistence.converter.AlertRecordConverter;
import org.niiish32x.sugarsms.alert.persistence.dao.AlertRecordDAO;
import org.niiish32x.sugarsms.common.result.PageResult;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AlertRecordRepoImpl
 *
 * @author shenghao ni
 * @date 2024.12.16 18:25
 */


@Repository
public class AlertRecordRepoImpl implements AlertRecordRepo {


    AlertRecordConverter converter = AlertRecordConverter.INSTANCE;

    @Resource
    private AlertRecordDAO alertRecordDAO;


    @Override
    public List<Long> findPendingSendEmailAlertIds(int recordCounts) {

        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getType, MessageType.EMAIL.name())
                // 未发送完成
                .eq(AlertRecordDO::getStatus, 0)
                // 未过期
//                .eq(AlertRecordDO::getExpire, 0)
                .list();

        List<AlertRecordDO> alertRecordDOS = list.subList(0, Math.min(recordCounts, list.size()));

        return alertRecordDOS.stream().map(AlertRecordDO::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findPendingSendSmsAlertIds(int recordCounts) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getType, MessageType.SMS.name())
                // 未发送完成
                .eq(AlertRecordDO::getStatus, 0)
                // 未过期
//                .eq(AlertRecordDO::getExpire, 0)
                .list();

        List<AlertRecordDO> alertRecordDOS = list.subList(0, Math.min(recordCounts, list.size()));

        return alertRecordDOS.stream().map(AlertRecordDO::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> findByAlertIdsByStatus(boolean status) {
        return alertRecordDAO.findByAlertIdsByStatus(status);
    }

    @Override
    public List<AlertRecordEO> findAlertsBeforeDays(Integer days) {
        List<AlertRecordDO> list = alertRecordDAO.findAlertBeforeDays(days);
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public List<AlertRecordEO> find() {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery().list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public List<AlertRecordEO> findByAlertId(Long alertId) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getAlertId, alertId)
                .list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public AlertRecordEO findById(Long id) {
        AlertRecordDO recordDO = alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getId, id).one();
        return converter.toEO(recordDO);
    }

    @Override
    public List<Long> findExistingAlertIds(List<Long> AlertIds) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .in(AlertRecordDO::getAlertId, AlertIds)
                .list();
        return list.stream().map(AlertRecordDO::getAlertId).collect(Collectors.toList());
    }

    @Override
    public List<AlertRecordEO> findByAlertId(MessageType type, boolean status) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getStatus, status)
                .eq(AlertRecordDO::getType,type)
                .list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public boolean save(AlertRecordEO alertRecordEO) {
        AlertRecordDO alertRecordDO = converter.toDO(alertRecordEO);

        AlertRecordDO one = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getAlertId, alertRecordDO.getAlertId())
                .eq(AlertRecordDO::getType, alertRecordDO.getType())
                .eq(AlertRecordDO::getUsername, alertRecordDO.getUsername())
                .one();

        if (one != null) {
            alertRecordDO.setId(one.getId());
            alertRecordDAO.updateById(alertRecordDO);
        }

        return alertRecordDAO.save(alertRecordDO);
    }

    @Override
    public boolean update(AlertRecordEO alertRecordEO) {
        AlertRecordDO alertRecordDO = converter.toDO(alertRecordEO);
        return alertRecordDAO.updateById(alertRecordDO);
    }

    @Override
    public boolean save(List<AlertRecordEO> alertRecordEOS) {
        List<AlertRecordDO> list = alertRecordEOS.stream().map(converter::toDO).collect(Collectors.toList());
        return alertRecordDAO.saveBatch(list);
    }

    @Override
    public boolean saveUniByReceiver(List<AlertRecordEO> alertRecordEOS) {

        for (AlertRecordEO alertRecordEO : alertRecordEOS) {
            AlertRecordDO alertRecordDO = converter.toDO(alertRecordEO);

            AlertRecordDO existAlertDO = null;

            if (alertRecordEO.getType() == MessageType.SMS) {
                existAlertDO = alertRecordDAO.lambdaQuery()
                        .eq(AlertRecordDO::getAlertId, alertRecordDO.getAlertId())
                        .eq(AlertRecordDO::getType, alertRecordDO.getType())
                        .eq(AlertRecordDO::getPhone, alertRecordDO.getPhone()).one();
            }

            if (alertRecordEO.getType() == MessageType.EMAIL){
                existAlertDO = alertRecordDAO.lambdaQuery()
                        .eq(AlertRecordDO::getAlertId, alertRecordDO.getAlertId())
                        .eq(AlertRecordDO::getType, alertRecordDO.getType())
                        .eq(AlertRecordDO::getEmail, alertRecordDO.getEmail()).one();
            }


            if (existAlertDO != null) {
                return true;
            }

            boolean saveRes = alertRecordDAO.save(alertRecordDO);
            Preconditions.checkArgument(saveRes, "保存失败");
        }

        return true;
    }

    @Override
    public List<AlertRecordEO> findFailRecords() {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getStatus, 0).list();
        return list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public Long findDurationFromStatToEnd(long alertId) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery().eq(AlertRecordDO::getAlertId, alertId).list();

        long start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;

        for (AlertRecordDO alertRecordDO : list) {
            start = Math.min(alertRecordDO.getSendTime().getTime(),start);
            end = Math.max(alertRecordDO.getSendTime().getTime(),end);
        }

        return end - start;
    }

    @Override
    public Long findDurationFromStatToEnd(long alertId, MessageType type) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getAlertId, alertId)
                .eq(AlertRecordDO::getType,type)
                .list();

        long start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;

        for (AlertRecordDO alertRecordDO : list) {
            start = Math.min(alertRecordDO.getSendTime().getTime(),start);
            end = Math.max(alertRecordDO.getSendTime().getTime(),end);
        }

        return end - start;
    }

    @Override
    public Long findDurationFromStatToEnd(MessageType type) {
        List<AlertRecordDO> list = alertRecordDAO.lambdaQuery()
                .eq(AlertRecordDO::getType,type)
                .list();

        long start = Long.MAX_VALUE;
        long end = Long.MIN_VALUE;

        for (AlertRecordDO alertRecordDO : list) {
            start = Math.min(alertRecordDO.getSendTime().getTime(),start);
            end = Math.max(alertRecordDO.getSendTime().getTime(),end);
        }

        return end - start;
    }

    @Override
    public Boolean remove(List<Long> ids) {
        return alertRecordDAO.removeBatchByIds(ids);
    }

    @Override
    public boolean updateStatusById(Long id, boolean status) {
        return alertRecordDAO.updateStatusById(id,status);
    }

    @Override
    public boolean updateExpireById(Long id, boolean expire) {
        return alertRecordDAO.updateExpireById(id,expire);
    }

    @Override
    public PageResult<AlertRecordEO> page(long current, long limit) {
        Page<AlertRecordDO>  page = Page.of(current,limit);
        Page<AlertRecordDO> pageRes = alertRecordDAO.page(page);
        List<AlertRecordEO> alertRecordEOList = pageRes.getRecords().stream().map(converter::toEO).collect(Collectors.toList());
        return PageResult.of(pageRes.getTotal(),alertRecordEOList);
    }

    @Override
    public Long countAlertRecords(boolean total ,boolean status) {

        if (total) {
            return alertRecordDAO.count();
        }

        LambdaQueryWrapper<AlertRecordDO> wrapper = Wrappers.<AlertRecordDO>lambdaQuery()
                .eq(AlertRecordDO::getStatus, status ? 1 : 0);

        long count = alertRecordDAO.count(wrapper);
        return count;
    }


}
