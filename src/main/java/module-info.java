module core {
    exports org.crudfeed.core.entity;
    exports org.crudfeed.core.dto.account;
    exports org.crudfeed.core.dto.message;
    exports org.crudfeed.core.dto.mod;
    exports org.crudfeed.core.dto.post;
    exports org.crudfeed.core.service;

    requires jakarta.persistence;
    requires java.logging;
    requires static lombok;
    requires org.hibernate.orm.core;
    requires spring.security.crypto;
    requires java.naming;
    requires com.fasterxml.jackson.annotation;
    requires spring.context;
    requires jdk.jfr;
}