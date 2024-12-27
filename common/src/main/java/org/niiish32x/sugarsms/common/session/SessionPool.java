package org.niiish32x.sugarsms.common.session;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.Session;


/**
 * SessionPool
 *
 * @author shenghao ni
 * @date 2024.12.27 11:35
 */
@Component
public class SessionPool {
    private final GenericObjectPool<Session> mailSessionPool;


    // 强制依赖注入
    @Autowired
    public SessionPool(MailSessionFactory mailSessionFactory) {
        GenericObjectPoolConfig<Session> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(30); // 最大连接数
        config.setMaxIdle(10);  // 最大空闲连接数
        config.setMinIdle(10);  // 最小空闲连接数

        mailSessionPool = new GenericObjectPool<>(new BasePooledObjectFactory<Session>() {
            @Override
            public Session create() {
                return mailSessionFactory.createMailSession();
            }

            @Override
            public PooledObject<Session> wrap(Session session) {
                return new DefaultPooledObject<>(session);
            }
        }, config);
    }

    public Session borrowMailSession() throws Exception {
        return mailSessionPool.borrowObject();
    }

    public void returnSession(Session session) {
        mailSessionPool.returnObject(session);
    }

    public void close() {
        mailSessionPool.close();
    }
}
