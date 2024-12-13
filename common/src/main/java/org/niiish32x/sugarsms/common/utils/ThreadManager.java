package org.niiish32x.sugarsms.common.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ThreadManager
 *
 * @author shenghao ni
 * @date 2024.12.11 18:00
 */
public class ThreadManager implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(ThreadManager.class);

    private static String SHARE_THREAD = "sugarsms-shared-pool";

    private ScheduledExecutorService SHARED_SCHEDULE;

    public static ThreadManager INSTANCE = null;

    private ConcurrentHashMap<String, ScheduledThreadPoolExecutor> SCH_THREAD_MAP = new ConcurrentHashMap();

    private AtomicBoolean inited = new AtomicBoolean(false);

    private ThreadManager() {
    }

    public static ThreadManager getInstance() {
        if (INSTANCE == null) {
            Class var0 = ThreadManager.class;
            synchronized (ThreadManager.class) {
                if (INSTANCE == null) {
                    ThreadManager manager = new ThreadManager();
                    manager.init();
                    INSTANCE = manager;
                }
            }
        }

        return INSTANCE;
    }

    public void init() {
        if (this.inited.compareAndSet(false,true)) {
            this.SHARED_SCHEDULE = this.allocSchPool(1, SHARE_THREAD, true);
            this.SHARED_SCHEDULE.scheduleAtFixedRate(this::checkThreadMetrics, 1L, 1L, TimeUnit.MINUTES);
            LOG.info("ThreadManager init success!");
        }
    }


    public ScheduledExecutorService allocSchPool(int core,String name,boolean daemon) {
        return (ScheduledExecutorService) this.SCH_THREAD_MAP.computeIfAbsent(name,(p) ->{
            return new ScheduledThreadPoolExecutor(core,(new ThreadFactoryBuilder()).setNamePrefix(name).setDaemon(daemon).build())
        })
    }


}





















