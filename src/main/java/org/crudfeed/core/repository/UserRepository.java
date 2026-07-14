package org.crudfeed.core.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.util.HibernateUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserRepository {

    private final SessionFactory sessionFactory;
    private final BCryptPasswordEncoder encoder;


    public UserRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.encoder = new BCryptPasswordEncoder();
    }

    public User signUp(User user) {
        user.setBio("");
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByNickname(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User WHERE nickname = :nick", User.class)
                    .setParameter("nick", nickname)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String subscribe(long subscriberId, String name) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User subscriber = session.get(User.class, subscriberId);

            User userToSubscribe = session.createQuery(
                            "FROM User WHERE nickname = :name", User.class
                    ).setParameter("name", name)
                    .uniqueResult();

            if (subscriber == null || userToSubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId() == userToSubscribe.getId()) {
                return "Cannot subscribe to yourself!";
            }

            if (subscriber.getSubscriptions().contains(userToSubscribe)) {
                return "User is already subscribed!";
            }

            subscriber.getSubscriptions().add(userToSubscribe);

            transaction.commit();

            return "User subscribed!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong!";
        }
    }

    public String unsubscribe(long subscriberId, String name) {
        try (Session session = sessionFactory.openSession()) {

            User subscriber = session.get(User.class, subscriberId);

            User userToUnsubscribe = session.createQuery(
                            "FROM User WHERE nickname = :name", User.class
                    ).setParameter("name", name)
                    .uniqueResult();

            if (subscriber == null || userToUnsubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId() == userToUnsubscribe.getId()) {
                return "Cannot unsubscribe from yourself!";
            }

            Transaction transaction = session.beginTransaction();

            if (!subscriber.getSubscriptions().contains(userToUnsubscribe)) {
                transaction.rollback();
                return "User is not subscribed!";
            }

            subscriber.getSubscriptions().remove(userToUnsubscribe);

            transaction.commit();

            return "Unsubscribed successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error!";
        }
    }

    public long getSubscribersCount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                            "SELECT COUNT(*) FROM user_subscribes WHERE subscribed_user_id = :id",
                            Long.class)
                    .setParameter("id", userId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getSubscriptionsCount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                            "SELECT COUNT(*) FROM user_subscribes WHERE user_id = :id",
                            Long.class)
                    .setParameter("id", userId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public User viewAccountPage(String nickname) {
        try (Session session = sessionFactory.openSession()) {

            User user = session.createQuery(
                            "FROM User u WHERE u.nickname = :name",
                            User.class)
                    .setParameter("name", nickname)
                    .uniqueResult();

            if (user != null) {
                user.getSubscriptions().size();
            }

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean editAccountPage(long userId, String bio) {
        String hql = """
            update User set bio = :userBio where id = :userId
            """;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            int updatedRows = session.createMutationQuery(hql)
                    .setParameter("userBio", bio)
                    .setParameter("userId", userId)
                    .executeUpdate();

            transaction.commit();

            return updatedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getNicknameById(long userId) {
        try (Session session = sessionFactory.openSession()) {
            String nickname = session.createQuery(
                            "select u.nickname from User u where u.id = :user_id", String.class)
                    .setParameter("user_id", userId)
                    .uniqueResult();

            return nickname != null ? nickname : "";

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public long getUserId(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            Long id = session.createQuery("select u.id from User u where u.nickname = :name", Long.class)
                    .setParameter("name", nickname)
                    .uniqueResult();

            return id != null ? id : 0L;

        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public boolean newName(String nickname, long user_id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, user_id);

            if (user != null) {
                user.setNickname(nickname);

                transaction.commit();
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean newPass(String incomingHash, long user_id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            User user = session.get(User.class, user_id);

            if (user != null) {
                String bcryptHash = encoder.encode(incomingHash);
                user.setPassword(bcryptHash);

                transaction.commit();
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(long user_id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            session.createMutationQuery("delete from Post where userId = :id")
                    .setParameter("id", user_id)
                    .executeUpdate();

            session.createMutationQuery("delete from Message where senderId = :id or receiverId = :id")
                    .setParameter("id", user_id)
                    .executeUpdate();

            int deletedUsers = session.createMutationQuery("delete from User where id = :id")
                    .setParameter("id", user_id)
                    .executeUpdate();

            transaction.commit();

            return deletedUsers > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}