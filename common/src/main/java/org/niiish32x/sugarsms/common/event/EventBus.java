package org.niiish32x.sugarsms.common.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * EventBus
 *
 * @author shenghao ni
 * @date 2025.01.22 14:51
 */

@Component
public class EventBus implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;
    private static EventBus instance;

    @PostConstruct
    void init() {
        instance = this;
    }

    public static void publishEvent(ApplicationEvent event) {
        instance.publisher.publishEvent(event);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        publisher = applicationEventPublisher;
    }
}
