package org.crudfeed.core.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.crudfeed.core.entity.Message;
import org.crudfeed.core.util.HibernateUtil;
import org.hibernate.Transaction;

import java.util.List;

public class MessageRepository {

    private final SessionFactory sessionFactory;

    public MessageRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public boolean writeMessage(long senderId, long receiverId, String text) {
        Message message = new Message(senderId, receiverId, text);

        try (Session session = sessionFactory.openSession()) {

            session.beginTransaction();
            session.persist(message);
            session.getTransaction().commit();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
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

    public boolean editMessage(long messageId, String text, long senderId) {
        String hql = """
            update Message
            set text = :text
            where id = :messageId
            and senderId = :senderId
            """;

        try (Session session = sessionFactory.openSession()) {

            Transaction transaction = session.beginTransaction();

            int updatedRows = session.createMutationQuery(hql)
                    .setParameter("text", text)
                    .setParameter("messageId", messageId)
                    .setParameter("senderId", senderId)
                    .executeUpdate();

            transaction.commit();

            return updatedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMessage(long messageId, long senderId) {
        String hql = """
            delete from Message
            where id = :messageId
            and senderId = :senderId
            """;

        try (Session session = sessionFactory.openSession()) {

            Transaction transaction = session.beginTransaction();

            int deletedRows = session.createMutationQuery(hql)
                    .setParameter("messageId", messageId)
                    .setParameter("senderId", senderId)
                    .executeUpdate();

            transaction.commit();

            return deletedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}