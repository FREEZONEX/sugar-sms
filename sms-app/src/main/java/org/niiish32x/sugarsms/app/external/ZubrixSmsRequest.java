package org.niiish32x.sugarsms.app.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SMSMessageDTO
 *
 * @author shenghao ni
 * @date 2024.12.09 9:47
 */


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZubrixSmsRequest {

    private String user;

    private String password;

    private String apiKey;
    private String senderId;
    private String channel;

    private String dcs;

    private String flashSMS;

    private String number;

    private String text;

    private String route;

    private String dltTemplateId;


    private String peid;

    public void CreateSMSRequest(String number,String text) {
        this.number = number;
        this.text = text;
        this.apiKey =  "hyRZM0cdlk2Ey4FzBM6qiA";
        this.senderId = "DSMDPR";
        this.channel = "Trans";
        this.dcs = "";
        this.flashSMS = "0";
        this.route = "15";
        this.dltTemplateId = "1607100000000331206";
        this.peid = "1601100000000014322";
    }
}
