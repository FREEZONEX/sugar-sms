package org.niiish32x.sugarsms.api.person.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import org.niiish32x.sugarsms.common.request.PageDTO;
import org.niiish32x.sugarsms.common.request.PageResponse;


import java.util.List;

/**
 * PersonsResponse
 *
 * @author shenghao ni
 * @date 2024.12.10 16:50
 */
@Data
public class PersonsResponse extends PageResponse {
    @JSONField(name = "list")
    private List<PersonDTO> list;
    @JSONField(name = "pagination")
    private PageDTO pageDTO;
}