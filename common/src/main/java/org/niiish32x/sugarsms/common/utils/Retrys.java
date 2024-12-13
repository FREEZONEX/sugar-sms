package org.niiish32x.sugarsms.common.utils;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Retrys
 *
 * @author shenghao ni
 * @date 2024.12.10 11:23
 */
public class Retrys {

    /**
     * 重试
     *
     * @param action    重试操作
     * @param condition 操作成功校验
     * @param retry     重试次数(< 0 表示一直重试，直到success或者break)
     * @param sleepMs   重试间隔
     * @param <T>
     * @return
     * @throws InterruptedException 当执行线程中断时,直接抛出中断异常，调用方选择是否响应中断
     * @throws BreakException       当 action、condition 执行过程中抛出BreakException时
     * @throws Throwable            当 重试次数耗完 && 操作未执行成功 && 存在执行异常 会将异常抛出
     */
    public static <T> T doWithRetry(Supplier<T> action, Predicate<T> condition, int retry, long sleepMs) throws Throwable {
        boolean forever = retry < 0;
        T result = null;
        Throwable throwable = null;
        boolean suc = false;
        while (forever || retry > 0) {
            try {
                result = action.get();
                if (suc = condition.test(result)) {
                    break;
                }
            } catch (BreakException e) {
                // 重试失败, 抛出失败根异常
                throw e.getCause();
            } catch (Throwable e) {
                // 其他异常忽略
                throwable = e;
                continue;
            } finally {
                if (!forever) {
                    retry--;
                }
            }
            Thread.sleep(sleepMs);
        }
        if (!suc && throwable != null) {
            throw throwable;
        }
        return result;
    }


    public static class BreakException extends RuntimeException {

        public BreakException(Throwable cause) {
            super(cause);
        }
    }
}
