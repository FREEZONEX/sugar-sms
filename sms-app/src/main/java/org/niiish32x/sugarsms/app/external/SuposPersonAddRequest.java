package org.niiish32x.sugarsms.app.external;

import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * SuposPersonAddRequest
 *
 * @author shenghao ni
 * @date 2024.12.09 14:40
 */

@Data
public class SuposPersonAddRequest implements Serializable {
    @NonNull
    private String code;
    @NonNull
    private String name;

    private String phone = "919747934655";
    @NonNull
    private String gender = "male";
    @NonNull
    private String status = "onWork";
    private String email;
    private String description;
    private String directLeaderCode;
    private String grandLeaderCode;
    private String entryDate;
    private String title;
    private String qualification;
    private String education;
    private String major;
    private String idNumber;
    @NonNull
    private String mainPositionCode = "standard_position";

    public SuposPersonAddRequest (String code) {
        this.code = code;
        this.name = code;
    }
}

