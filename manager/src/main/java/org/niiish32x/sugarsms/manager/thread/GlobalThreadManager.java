package org.niiish32x.sugarsms.manager.thread;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ThreadManager
 *
 * @author shenghao ni
 * @date 2025.01.05 13:44
 */
@Slf4j
public class GlobalThreadManager implements Serializable {
    private static String SHARE_THREAD = "sugar-sms-schedule-pool";

    private ConcurrentHashMap<String, Thread> SINGLE_THREAD_MAP = new ConcurrentHashMap();
    private ConcurrentHashMap<String, ThreadPoolExecutor> POOL_THREAD_MAP = new ConcurrentHashMap();
    private ConcurrentHashMap<String, ScheduledThreadPoolExecutor> SCH_THREAD_MAP = new ConcurrentHashMap();
    private ScheduledExecutorService SHARED_SCHEDULE;

    private AtomicBoolean inited = new AtomicBoolean(false);
    private static GlobalThreadManager INSTANCE = null;

    public static GlobalThreadManager getInstance() {
        if (INSTANCE == null) {
            Class<GlobalThreadManager> var0 = GlobalThreadManager.class;
            synchronized(GlobalThreadManager.class) {
                if (INSTANCE == null) {
                    GlobalThreadManager manager = new GlobalThreadManager();
                    manager.init();
                    INSTANCE = manager;
                }
            }
        }

        return INSTANCE;
    }

    private GlobalThreadManager() {
    }

    public void init() {
        if (this.inited.compareAndSet(false, true)) {
            this.SHARED_SCHEDULE = this.allocSchPool(1, SHARE_THREAD, true);
            log.info("ThreadManager init success!");
        }
    }

    public Thread allocThread(String name, boolean daemon, Runnable run) {
        return this.SINGLE_THREAD_MAP.computeIfAbsent(name, (n) -> {
            Thread thread = new Thread(run, name);
            thread.setDaemon(daemon);
            thread.setName(name);
            return thread;
        });
    }

    public ThreadPoolExecutor allocPool(int core, int max, long aliveMs, int queue, String name, boolean daemon) {
        return this.allocPool(core, max, aliveMs, queue, name, daemon, (r, e) -> {
            throw new RejectedExecutionException(name + " pool reject task");
        });
    }

    public ThreadPoolExecutor allocPool(int core, int max, long aliveMs, int queue, String name, boolean daemon, RejectedExecutionHandler reject) {
        return this.POOL_THREAD_MAP.computeIfAbsent(name, (p) -> new ThreadPoolExecutor(core, max, aliveMs, TimeUnit.MILLISECONDS, queue > 0 ? new LinkedBlockingQueue(queue) : new SynchronousQueue(), (new GlobalThreadFactoryBuilder()).setNameFormat(name + "-%d").setDaemon(daemon).build(), reject));
    }

    public ScheduledExecutorService allocSchPool(int core, String name, boolean daemon) {
        return this.SCH_THREAD_MAP.computeIfAbsent(name, (p) -> new ScheduledThreadPoolExecutor(core, (new GlobalThreadFactoryBuilder()).setNameFormat(name + "-%d").setDaemon(daemon).build()));
    }
    public ScheduledExecutorService sharedSchPool() {
        Preconditions.checkArgument(this.SHARED_SCHEDULE != null, "Thread must init first");
        return this.SHARED_SCHEDULE;
    }



    public void closePool(String name) {
        ThreadPoolExecutor pool = (ThreadPoolExecutor)this.POOL_THREAD_MAP.remove(name);
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            log.info("thread {} pool try  shutdown", name);
        }

    }

    public void close() {
        Collection<Thread> singalThreads = this.SINGLE_THREAD_MAP.values();
        String name = "";
        if (!singalThreads.isEmpty()) {
            for(Iterator<Thread> var2 = singalThreads.iterator(); var2.hasNext(); log.info("thread {} close", name)) {
                Thread t = (Thread)var2.next();
                name = t.getName();
                if (t.isAlive()) {
                    t.interrupt();
                }
            }
        }

        Set<Map.Entry<String, ThreadPoolExecutor>> pools = this.POOL_THREAD_MAP.entrySet();
        if (!pools.isEmpty()) {
            Iterator<Map.Entry<String, ThreadPoolExecutor>> var9 = pools.iterator();

            while(var9.hasNext()) {
                Map.Entry<String, ThreadPoolExecutor> entry = var9.next();
                name = entry.getKey();
                ThreadPoolExecutor pool = entry.getValue();
                pool.shutdown();
                log.info("thread {} pool try  shutdown", name);
            }
        }

        Set<Map.Entry<String, ScheduledThreadPoolExecutor>> schPools = this.SCH_THREAD_MAP.entrySet();
        if (!pools.isEmpty()) {
            Iterator<Map.Entry<String, ScheduledThreadPoolExecutor>> var12 = schPools.iterator();

            while(var12.hasNext()) {
                Map.Entry<String, ScheduledThreadPoolExecutor> entry = (Map.Entry)var12.next();
                name = entry.getKey();
                ScheduledThreadPoolExecutor pool = entry.getValue();
                pool.shutdown();
                log.info("thread {} pool try  shutdown", name);
            }
        }

    }
}
