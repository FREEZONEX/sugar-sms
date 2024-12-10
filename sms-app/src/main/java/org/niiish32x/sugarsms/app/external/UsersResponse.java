package org.niiish32x.sugarsms.app.external;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.common.supos.request.PageResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * UsersResponse
 *
 * @author shenghao ni
 * @date 2024.12.10 17:31
 */
@Data

public class UsersResponse extends PageResponse implements Serializable {
    @JSONField(name = "list")
    private List<SuposUserDTO> list;

    public UsersResponse(){
        this.list = new ArrayList<>();
    }
}