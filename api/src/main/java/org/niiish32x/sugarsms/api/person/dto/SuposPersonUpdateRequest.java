package org.niiish32x.sugarsms.api.person.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.checkerframework.checker.units.qual.N;

/**
 * SuposPersonUpdateRequest
 *
 * @author shenghao ni
 * @date 2024.12.14 17:05
 */

@Data
@Builder
public class SuposPersonUpdateRequest {
    @NonNull
    private String code;
    @NonNull
    private String name;

    private String phone;

    @NonNull
    private String gender;

    @NonNull
    private String status;

    @NonNull
    private String mainPositionCode;
}
