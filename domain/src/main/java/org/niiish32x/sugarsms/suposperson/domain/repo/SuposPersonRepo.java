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
    List<SuposPersonEO> find();
}
