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
                transaction.rollback();
                e.printStackTrace();
                return false;
            }
        }
    }

    public List<Message> viewMessages(long senderId, long receiverId) {
        String hql = """
            FROM Message m 
            WHERE (m.senderId = :sId AND m.receiverId = :rId) 
               OR (m.senderId = :rId AND m.receiverId = :sId)
            ORDER BY m.createdAt ASC
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
        String hql = """
            update Message
            set content = :content
            where id = :messageId
            and senderId = :senderId
            """;

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(hql)
                        .setParameter("content", content)
                        .setParameter("messageId", messageId)
                        .setParameter("senderId", senderId)
                        .executeUpdate();

                transaction.commit();

                return updatedRows > 0;
            } catch (Exception e) {
                transaction.rollback();
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
                                "delete from Message where id = :messageId and senderId = :senderId"
                        )
                        .setParameter("messageId", messageId)
                        .setParameter("senderId", senderId)
                        .executeUpdate();

                transaction.commit();

                return deletedRows > 0;
            } catch (Exception e) {
                transaction.rollback();
                e.printStackTrace();
                return false;
            }
        }
    }
}