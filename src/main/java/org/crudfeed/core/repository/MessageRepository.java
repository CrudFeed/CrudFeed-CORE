package org.crudfeed.core.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.crudfeed.core.entity.Message;
import org.crudfeed.core.util.HibernateUtil;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepository {

    private final SessionFactory sessionFactory;

    public MessageRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public boolean writeMessage(Message message) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(message);
                transaction.commit();
                return true;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<Message> viewMessages(long senderId, long receiverId) {
        String hql = """
            from Message m 
            where (m.senderId = :sId and m.receiverId = :rId and m.deleted = false) 
               or (m.senderId = :rId and m.receiverId = :sId and m.deleted = false)
            order by m.createdAt asc
            """;

        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(hql, Message.class)
                    .setParameter("sId", senderId)
                    .setParameter("rId", receiverId)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public boolean editMessage(long messageId, String content, long senderId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                                "update Message m " +
                                        "set m.content = :content " +
                                        "where m.id = :messageId " +
                                        "and m.senderId = :senderId " +
                                        "and m.deleted = false"
                        )
                        .setParameter("content", content)
                        .setParameter("messageId", messageId)
                        .setParameter("senderId", senderId)
                        .executeUpdate();

                transaction.commit();

                return updatedRows > 0;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteMessage(long messageId, long senderId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int deletedRows = session.createMutationQuery(
                                "update Message m " +
                                        "set m.deleted = true " +
                                        "where m.id = :messageId and " +
                                        "m.senderId = :senderId and " +
                                        "m.deleted = false"
                        )
                        .setParameter("messageId", messageId)
                        .setParameter("senderId", senderId)
                        .executeUpdate();

                transaction.commit();

                return deletedRows > 0;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }
}