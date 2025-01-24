package org.niiish32x.sugarsms.app.startup;

import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.alert.app.AlertService;
import org.niiish32x.sugarsms.api.person.dto.SuposPersonDTO;
import org.niiish32x.sugarsms.common.enums.CompanyEnum;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.suposperson.app.SuposPersonService;
import org.niiish32x.sugarsms.suposperson.app.command.SavePersonCommand;
import org.niiish32x.sugarsms.suposperson.app.external.PersonPageQueryRequest;
import org.niiish32x.sugarsms.suposperson.domain.repo.SuposPersonRepo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Starup
 *
 * @author shenghao ni
 * @date 2025.01.21 16:41
 */

@Slf4j
@Component
public class Startup implements InitializingBean {

    @Autowired
    AlertService alertService;

    @Autowired
    SuposPersonService suposPersonService;

    @Autowired
    SuposPersonRepo suposPersonRepo;

    @Override
    public void afterPropertiesSet() throws Exception {
//        log.info("start ack all old alerts");
//        // 确认所有消息 防止重启后一次性发送过多
//        alertService.ackAlerts();
//        log.info("finish ack all old alerts");
        log.info(">>>>>>>>>> start load  person >>>>>>>>>>>>>>");
        loadAllPerson();
    }

    // 预先加载一部分 person 到数据库
    void loadAllPerson () {
        int pageNo = 1;

        while (true) {

            Result<List<SuposPersonDTO>> peronFromSupos = suposPersonService.searchPeronFromSupos(PersonPageQueryRequest.builder()
                    .companyCode(CompanyEnum.DEFAULT.value)
                            .pageNo(pageNo)
                            .pageSize(20)
                    .build());

            List<SuposPersonDTO> personDTOS = peronFromSupos.getData();

            if (personDTOS == null || personDTOS.isEmpty()) {
                break;
            }

            for (SuposPersonDTO personDTO : personDTOS) {
                suposPersonService.savePerson(new SavePersonCommand(personDTO));
            }
            pageNo++;
        }



    }
}
