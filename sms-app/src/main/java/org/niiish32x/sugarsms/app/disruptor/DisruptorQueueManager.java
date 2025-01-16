package org.niiish32x.sugarsms.app.disruptor;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.niiish32x.sugarsms.app.disruptor.alert.consumer.DisruptorMqAlertRecordConsumer;
import org.niiish32x.sugarsms.app.disruptor.alert.consumer.DisruptorMqConsumer;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertMessgaeEventFactory;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEvent;
import org.niiish32x.sugarsms.app.disruptor.alert.event.AlertRecordEventFactory;
import org.niiish32x.sugarsms.manager.thread.GlobalThreadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * MessageQueueManager
 *
 * @author shenghao ni
 * @date 2025.01.16 11:20
 *
 * 参考 https://tech.meituan.com/2016/11/18/disruptor.html
 */

@Configuration
public class DisruptorQueueManager {

    static int maximumPoolSize = 640;
    static int coolPoolSize = 32;

    /**
     * Disruptor 多出来拒绝就可以了 交由Disruptor 进行调度
     */
    static RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

    private static final ThreadPoolExecutor poolExecutor = GlobalThreadManager.getInstance().allocPool(coolPoolSize, maximumPoolSize,
            10 * 60 * 1000, 3000, "sugar-sms-disruptor-pool", true ,handler);


    @Autowired
    DisruptorMqAlertRecordConsumer disruptorMqAlertRecordConsumer;

    @Bean
    public RingBuffer<AlertRecordEvent> alertRecordRingBuffer() {

        AlertRecordEventFactory eventFactory = new AlertRecordEventFactory();

        int ringBufferSize = 1024 * 1024 * 16;

        Disruptor<AlertRecordEvent> disruptor = new Disruptor<>(
                eventFactory,
                ringBufferSize,
                poolExecutor,
                ProducerType.SINGLE, // single 就是一个 ringBuffer 只有一种类型event
                new YieldingWaitStrategy()
        );

        // 设置事件业务处理器---消费者
        disruptor.handleEventsWith(disruptorMqAlertRecordConsumer);

        // 启动disruptor线程
        disruptor.start();

        // 获取ringbuffer环，用于接取生产者生产的事件
        return disruptor.getRingBuffer();
    }

    @Bean
    public RingBuffer<AlertEvent> alertMessageRingBuffer() {
        // 定义用于事件处理的线程池，
        // Disruptor通过java.util.concurrent.ExecutorSerivce提供的线程来触发consumer的事件处理// 指定事件工厂
        AlertMessgaeEventFactory eventFactory = new AlertMessgaeEventFactory();

        /**
         * ringBufferSize 指的是这个环形缓冲区的大小。它表示环形缓冲区中可以容纳的元素数量。 即队列大小
         */
        // 指定ringbuffer字节大小，必须为2的N次方（能将求模运算转为位运算提高效率），否则将影响效率
        // 1024 * 1024 = 1MB
        int ringBufferSize = 1024 * 1024 * 16;

        //决定一个消费者如何等待生产者将Event置入Disruptor,其所有实现都是针对消费者线程的 。
        //BlockingWaitStrategy：最低效的策略，但其对CPU的消耗最小，并且在各种部署环境中能提供更加一致的性能表现，内部维护了一个重入锁ReentrantLock和Condition
        //SleepingWaitStrategy: 性能表现和com.lmax.disruptor.BlockingWaitStrategy差不多，对CPU的消耗也类似，但其对生产者线程的影响最小，
//　　　　　　　　　　适合用于异步日志类似的场景；是一种无锁的方式
        //YieldingWaitStrategy: 性能最好，适合用于低延迟的系统；在要求极高性能且事件处理线程数小于CPU逻辑核心树的场景中，推荐使用此策略；
//　　　　　　　　　　例如，CPU开启超线程的特性；也是无锁的实现，只要是无锁的实现，signalAllWhenBlocking()都是空实现

        //SINGLE 采用单消费者模式
        //MULTI 多个消费者，每个消费者消费不同数据。也就是说每个消费者竞争数据，竞争到消费，其他消费者没有机会
        Disruptor<AlertEvent> disruptor = new Disruptor<>(
                eventFactory,
                ringBufferSize,
                poolExecutor,
                ProducerType.SINGLE, // single 就是一个 ringBuffer 只有一种类型event
                /**
                 * 等待策略
                 * BlockingWaitStrategy 加锁	 适用于: CPU资源紧缺，吞吐量和延迟并不重要的场景
                 * BusySpinWaitStrategy 自旋	 适用于: 通过不断重试，减少切换线程导致的系统调用，而降低延迟。推荐在线程绑定到固定的CPU的场景下使用
                 * YieldingWaitStrategy  自旋+yield+自旋 适用于: 性能和CPU资源之间有很好的折中。延迟比较均匀
                 */
                new YieldingWaitStrategy()
        );

        // 设置事件业务处理器---消费者
        disruptor.handleEventsWith(new DisruptorMqConsumer());

        // 启动disruptor线程
        disruptor.start();

        // 获取ringbuffer环，用于接取生产者生产的事件
        return disruptor.getRingBuffer();
    }
}
