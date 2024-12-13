package org.niiish32x.sugarsms.app.mq.impl;

import org.niiish32x.sugarsms.app.mq.AlertMessageConsumer;
import org.niiish32x.sugarsms.common.request.SuposRequestManager;

import javax.annotation.Resource;

/**
 * AlertMessageConsumerImpl
 *
 * @author shenghao ni
 * @date 2024.12.09 18:27
 */
public class AlertMessageConsumerImpl implements AlertMessageConsumer {

    @Resource
    SuposRequestManager requestManager;

    @Override
    public void pushMessage() {
//        Map<String, String> headerMap = new HashMap<>();
//        Map<String, String> queryMap = new HashMap<>();
//        requestManager.suposApiPost(ApiEnum.MESSAGE_POST_API.value,headerMap,queryMap);
    }
}
