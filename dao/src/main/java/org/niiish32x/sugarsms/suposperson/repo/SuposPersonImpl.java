package org.niiish32x.sugarsms.suposperson.repo;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.niiish32x.sugarsms.suposperson.SuposPersonDO;
import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;
import org.niiish32x.sugarsms.suposperson.domain.repo.SuposPersonRepo;
import org.niiish32x.sugarsms.suposperson.persistence.converter.SuposPersonConverter;
import org.niiish32x.sugarsms.suposperson.persistence.dao.SuposPersonDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SuposPersonImpl
 *
 * @author shenghao ni
 * @date 2025.01.09 9:36
 */

@Repository
public class SuposPersonImpl implements SuposPersonRepo {

    SuposPersonConverter converter = SuposPersonConverter.INSTANCE;

    @Autowired
    private SuposPersonDAO suposPersonDAO;


    @Override
    public List<SuposPersonEO> findByCode() {
        List<SuposPersonDO> list = suposPersonDAO.lambdaQuery().list();
        return  list.stream().map(converter::toEO).collect(Collectors.toList());
    }

    @Override
    public SuposPersonEO findByCode(String code) {
        List<SuposPersonDO> list = suposPersonDAO.lambdaQuery().eq(SuposPersonDO::getCode, code).list();

        if (list == null || list.isEmpty()) {
            return null;
        }

        SuposPersonDO suposPersonDO = list.get(0);
        return converter.toEO(suposPersonDO);
    }

    @Override
    public boolean save(SuposPersonEO suposPersonEO) {
        SuposPersonDO suposPersonDO = converter.toDO(suposPersonEO);
        List<SuposPersonDO> list = suposPersonDAO.lambdaQuery().eq(SuposPersonDO::getCode, suposPersonDO.getCode()).list();

        if (list != null && !list.isEmpty()) {
            SuposPersonDO existPerson = list.get(0);
            LambdaUpdateWrapper<SuposPersonDO> wrapper = Wrappers.<SuposPersonDO>lambdaUpdate()
//                    .set(SuposPersonDO::getId, existPerson.getId())
                    .eq(SuposPersonDO::getCode, existPerson.getCode())
                    .set(SuposPersonDO::getName, suposPersonDO.getName())
                    .set(SuposPersonDO::getValid, suposPersonDO.getValid())
                    .set(SuposPersonDO::getStatus, suposPersonDO.getStatus())
                    .set(SuposPersonDO::getGender, suposPersonDO.getGender())
                    .set(SuposPersonDO::getMainPosition, suposPersonDO.getMainPosition())
                    .set(SuposPersonDO::getEntryDate, suposPersonDO.getEntryDate())
                    .set(SuposPersonDO::getTitle, suposPersonDO.getTitle())
                    .set(SuposPersonDO::getQualification, suposPersonDO.getQualification())
                    .set(SuposPersonDO::getEducation, suposPersonDO.getEducation())
                    .set(SuposPersonDO::getMajor, suposPersonDO.getMajor())
                    .set(SuposPersonDO::getIdNumber, suposPersonDO.getIdNumber())
                    .set(SuposPersonDO::getPhone, suposPersonDO.getPhone())
                    .set(SuposPersonDO::getEmail, suposPersonDO.getEmail())
                    .set(SuposPersonDO::getAvatarUrl, suposPersonDO.getAvatarUrl())
                    .set(SuposPersonDO::getSignUrl, suposPersonDO.getSignUrl())
                    .set(SuposPersonDO::getDepartments, suposPersonDO.getDepartments())
                    .set(SuposPersonDO::getCompanies, suposPersonDO.getCompanies())
                    .set(SuposPersonDO::getUser, suposPersonDO.getUser())
                    .set(SuposPersonDO::getPositions, suposPersonDO.getPositions())
                    .set(SuposPersonDO::getModifyTime, suposPersonDO.getModifyTime())
                    .set(SuposPersonDO::getDirectLeader, suposPersonDO.getDirectLeader())
                    .set(SuposPersonDO::getGrandLeader, suposPersonDO.getGrandLeader())
                    .set(SuposPersonDO::getDeleted, suposPersonDO.getDeleted());
//                .set(SuposPersonDO::getCreateTime, suposPersonDO.getCreateTime())
//                .set(SuposPersonDO::getUpdateTime, suposPersonDO.getUpdateTime());
            return  suposPersonDAO.update(existPerson, wrapper);
        }



        return suposPersonDAO.save(suposPersonDO);
    }

    @Override
    public boolean exist(String code) {
        return suposPersonDAO.lambdaQuery().eq(SuposPersonDO::getCode, code).exists();
    }


}
