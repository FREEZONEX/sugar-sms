package org.niiish32x.sugarsms.app.queue;

import lombok.extern.slf4j.Slf4j;

import org.niiish32x.sugarsms.api.alert.dto.AlertInfoDTO;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * AlertMessageQueue
 *
 * @author shenghao ni
 * @date 2024.12.15 13:13
 */

@Component
@Slf4j
public class AlertMessageQueue {
    static BlockingQueue<AlertInfoDTO> queue = new ArrayBlockingQueue(100);


    public boolean offer(AlertInfoDTO alertInfoDTO)  {
        try {
            queue.put(alertInfoDTO);
            return true;
        }catch (InterruptedException e) {
            log.error("报警信息入队异常: id {} {} ",alertInfoDTO.getId(),alertInfoDTO.getAlertName());
            return false;
        }
    }

    public AlertInfoDTO poll() {
        if (!queue.isEmpty()) {
            return queue.poll();
        }

        return  null;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
