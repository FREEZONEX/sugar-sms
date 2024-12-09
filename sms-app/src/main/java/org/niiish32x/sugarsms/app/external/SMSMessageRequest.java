package org.niiish32x.sugarsms.app.external;

import lombok.Data;

/**
 * SMSMessageDTO
 *
 * @author shenghao ni
 * @date 2024.12.09 9:47
 */


@Data
public class SMSMessageRequest {
    private String apiKey;
    private String senderId;
    private String channel;

    private String dcs;

    private String flashSMS;

    private String number;

    private String text;

    private String route;

    public void CreateSMSRequest(String number,String text) {
        this.number = number;
        this.text = number;
        this.apiKey =  "hyRZM0cdlk2Ey4FzBM6qiA";
        this.senderId = "DSMDPR";
        this.channel = "Trans";
        this.dcs = "";
        this.flashSMS = "0";
        this.route = "15";
    }
}
