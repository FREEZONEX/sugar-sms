package org.niiish32x.sugarsms.manager.session;

import javax.mail.Session;

/**
 * SessionFactory
 *
 * @author shenghao ni
 * @date 2025.01.05 15:02
 */
public interface SessionFactory {
    Session createMailSession();
}
