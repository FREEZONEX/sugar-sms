package org.niiish32x.sugarsms.suposperson.domain.repo;

import org.niiish32x.sugarsms.suposperson.domain.entity.SuposPersonEO;

import java.util.List;

/**
 * SuposPersonRepo
 *
 * @author shenghao ni
 * @date 2025.01.09 9:36
 */
public interface SuposPersonRepo {
    List<SuposPersonEO> findByCode();

    SuposPersonEO findByCode(String code);

    boolean save(SuposPersonEO suposPersonEO);

    boolean softRemove(SuposPersonEO suposPersonEO);

    /**
     * 根据人员编号 判断是否存在
     * @param code
     * @return
     */
    boolean exist(String code);
}
