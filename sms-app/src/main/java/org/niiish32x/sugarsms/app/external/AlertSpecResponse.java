package org.niiish32x.sugarsms.app.external;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.niiish32x.sugarsms.app.dto.AlertSpecDTO;
import org.niiish32x.sugarsms.app.dto.PageDTO;

import java.io.Serializable;
import java.util.List;

/**
 * AlertSpecResponse
 *
 * @author shenghao ni
 * @date 2024.12.25 10:19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertSpecResponse  implements Serializable {

    @JSONField(name = "list")
    List<AlertSpecDTO> list;

    @JSONField(name = "pagination")
    private PageDTO pageDTO;
}
