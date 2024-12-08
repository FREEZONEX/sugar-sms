package org.niiish32x.sugarsms.app.service.impl;

import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.dto.PageDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.enums.ApiEnum;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;
import org.niiish32x.sugarsms.common.supos.request.SuposRequestManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PersonServiceImpl
 *
 * @author shenghao ni
 * @date 2024.12.08 18:41
 */

@Service
@Slf4j
public class PersonServiceImpl implements PersonService {

    @Resource
    SuposRequestManager requestManager;


    @Data
    class PersonsResponse extends PageResponse {
        @JSONField(name = "list")
        private List<PersonDTO> list;
        @JSONField(name = "pagination")
        private PageDTO pageDTO;
    }

    @Override
    public List<PersonDTO> getPersonsFromSuposByPage(Integer currentPageSize) {
        Map<String, String> headerMap = new HashMap<>();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("current", String.valueOf(currentPageSize));
        HttpResponse response = requestManager.suposApiGet(ApiEnum.PESRON_API.value, headerMap, queryMap);

        PersonsResponse personsResponse = JSON.parseObject(response.body(),PersonsResponse.class);

        return personsResponse.getList();
    }
}
