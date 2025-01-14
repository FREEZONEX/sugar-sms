package org.niiish32x.sugarsms.user.app.external;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

import org.niiish32x.sugarsms.api.user.dto.RoleSpecDTO;
import org.niiish32x.sugarsms.common.request.PageResponse;

import java.util.List;

/**
 * RolePageResponse
 *
 * @author shenghao ni
 * @date 2024.12.25 16:30
 */

@Data
public class RolePageResponse extends PageResponse {
    @JSONField(name = "list")
    private List<RoleSpecDTO> list;
}
