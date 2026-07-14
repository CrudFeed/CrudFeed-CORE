package org.crudfeed.core.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;

@UtilityClass
public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory;

    static {
        Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        try {
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}