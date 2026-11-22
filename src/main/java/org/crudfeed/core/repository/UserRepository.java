package org.crudfeed.core.repository;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.util.HibernateUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;
    private final BCryptPasswordEncoder encoder;

    public UserRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.encoder = new BCryptPasswordEncoder();
    }

    public User signUp(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                return user;
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                throw new RuntimeException("Error saving user: " + e.getMessage(), e);
            }
        }
    }

    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByNickname(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User where nickname = :name", User.class)
                    .setParameter("name", nickname)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String subscribe(long subscriberId, String nickname) {
        try (Session session = sessionFactory.openSession()) {
            User subscriber = session.get(User.class, subscriberId);

            User userToSubscribe = session.createQuery(
                            "from User where nickname = :nickname", User.class)
                    .setParameter("nickname", nickname).uniqueResult();

            if (subscriber == null || userToSubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId() == userToSubscribe.getId()) {
                return "Cannot subscribe to yourself!";
            }

            Transaction transaction = session.beginTransaction();
            try {
                if (subscriber.getSubscriptions().contains(userToSubscribe)) {
                    transaction.rollback();
                    return "User is already subscribed!";
                }

                subscriber.getSubscriptions().add(userToSubscribe);
                transaction.commit();
                return "User subscribed!";
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return "Something went wrong!";
            }
        }
    }

    public String unsubscribe(long subscriberId, String nickname) {
        try (Session session = sessionFactory.openSession()) {
            User subscriber = session.get(User.class, subscriberId);

            User userToUnsubscribe = session.createQuery(
                            "from User where nickname = :nickname", User.class)
                    .setParameter("nickname", nickname).uniqueResult();

            if (subscriber == null || userToUnsubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId() == userToUnsubscribe.getId()) {
                return "Cannot unsubscribe from yourself!";
            }

            Transaction transaction = session.beginTransaction();
            try {
                if (!subscriber.getSubscriptions().contains(userToUnsubscribe)) {
                    transaction.rollback();
                    return "User is already unsubscribed!";
                }

                subscriber.getSubscriptions().remove(userToUnsubscribe);
                transaction.commit();
                return "User unsubscribed!";
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return "Something went wrong!";
            }
        }
    }

    public long getSubscribersCount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                            "select count(*) from user_subscribes where subscribed_user_id = :userId",
                            Long.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public long getSubscriptionsCount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                            "select count(*) from user_subscribes where user_id = :id",
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
                            "from User u where u.nickname = :name",
                            User.class)
                    .setParameter("name", nickname)
                    .uniqueResult();

            if (user != null) {
                Hibernate.initialize(user.getSubscriptions());
            }

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean editAccountPage(long userId, String bio) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery("update User set bio = :userBio where id = :userId")
                        .setParameter("userBio", bio)
                        .setParameter("userId", userId)
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

    public String getNicknameById(long userId) {
        try (Session session = sessionFactory.openSession()) {
            String nickname = session.createQuery(
                            "select u.nickname from User u where u.id = :userId", String.class)
                    .setParameter("userId", userId)
                    .uniqueResult();

            return nickname != null ? nickname : "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public long getUserId(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            Long id = session.createQuery(
                    "select u.id " +
                            "from User u " +
                            "where u.nickname = :name",
                     Long.class
                    )
                    .setParameter("name", nickname)
                    .uniqueResult();

            return id != null ? id : 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }

    public boolean newName(String nickname, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                        "update User " +
                                "set nickname = :nickname " +
                                "where id = :userId"
                        )
                        .setParameter("nickname", nickname)
                        .setParameter("userId", userId)
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

    public boolean newPass(String incomingHash, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, userId);

                if (user != null) {
                    String bcryptHash = encoder.encode(incomingHash);
                    user.setPassword(bcryptHash);

                    transaction.commit();
                    return true;
                }

                return false;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean deleteUser(long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.createMutationQuery("update Post p set deleted = true where p.userId = :userId")
                        .setParameter("userId", userId)
                        .executeUpdate();

                session.createMutationQuery(
                        "update Message m " +
                                "set deleted = true " +
                                "where m.senderId = :userId " +
                                "or m.receiverId = :userId"
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                int deletedUsers = session.createMutationQuery(
                        "update User u " +
                                "set deleted = true " +
                                "where u.id = :userId"
                        )
                        .setParameter("id", userId)
                        .executeUpdate();

                transaction.commit();

                return deletedUsers > 0;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean modBanAccount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int bannedUsers = session.createMutationQuery(
                                "update User u " +
                                        "set banned = true " +
                                        "where u.id = :userId"
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                if (bannedUsers == 0) {
                    transaction.rollback();
                    return false;
                }

                session.createMutationQuery(
                        "update Post p " +
                                "set banned = true " +
                                "where p.userId = :userId"
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                session.createMutationQuery(
                                "update Message m " +
                                        "set banned = true " +
                                        "where m.senderId = :userId "
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                transaction.commit();
                return bannedUsers > 0;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean modUnbanAccount(long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int unbannedUsers = session.createMutationQuery(
                                "update User u " +
                                        "set banned = false " +
                                        "where u.id = :userId"
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                if (unbannedUsers == 0) {
                    transaction.rollback();
                    return false;
                }

                session.createMutationQuery(
                                "update Post p " +
                                        "set banned = false " +
                                        "where p.userId = :userId"
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                session.createMutationQuery(
                                "update Message m " +
                                        "set banned = false " +
                                        "where m.senderId = :userId "
                        )
                        .setParameter("userId", userId)
                        .executeUpdate();

                transaction.commit();
                return unbannedUsers > 0;
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