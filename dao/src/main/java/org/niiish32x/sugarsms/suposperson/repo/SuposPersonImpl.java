package org.niiish32x.sugarsms.suposperson.repo;

import org.niiish32x.sugarsms.suposperson.SuposPersonDO;
import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;
import org.niiish32x.sugarsms.suposperson.domain.repo.SuposPersonRepo;
import org.niiish32x.sugarsms.suposperson.persistence.converter.SuposPersonConverter;
import org.niiish32x.sugarsms.suposperson.persistence.dao.SuposPersonDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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
    public List<SuposPersonEO> find() {
        List<SuposPersonDO> list = suposPersonDAO.lambdaQuery().list();
        return  list.stream().map(converter::toEO).collect(Collectors.toList());
    }
}
