package org.niiish32x.sugarsms.api.alert.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * AlertAckRequest
 *
 * @author shenghao ni
 * @date 2025.01.21 15:29
 */

// https://cloud.supos.com/open/APIs/detail?treeNodeCode=1390&openapiVersionCode=c3bc97b89d05458d8cef7ee0c810dd6b&apisManageCode=25353e4b1c614159ab4ffc3f9ddbb769&clickActive=supOS
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlertAckRequest implements Serializable {
    String ackAll;

    @NonNull
    String userName;

    List<String> fullNames;
}
