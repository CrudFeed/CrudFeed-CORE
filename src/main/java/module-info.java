module core {
    exports org.crudfeed.core.entity;
    requires jakarta.persistence;
    requires java.logging;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires spring.security.crypto;
    requires java.naming; requires com.fasterxml.jackson.annotation;
    requires spring.context;
    requires jdk.jfr;
}