package org.crudfeed.core.repository;

import org.crudfeed.core.dto.account.GetBannedDto;
import org.crudfeed.core.dto.account.ShowFriendsListDto;
import org.crudfeed.core.dto.account.ViewAccountPageDto;
import org.crudfeed.core.model.FriendFlags;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.util.HibernateUtil;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public User findById(long userId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User u " + "where u.id = :userId ", User.class)
                    .setParameter("userId", userId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByEmail(String email) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User u " + "where u.email = :email ", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User findByNickname(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from User u where u.nickname = :name", User.class)
                    .setParameter("name", nickname)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updateLastSeen(long userId, Instant lastSeen) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.createMutationQuery("update User set lastSeen = :lastSeen where id = :userId")
                        .setParameter("lastSeen", lastSeen)
                        .setParameter("userId", userId)
                        .executeUpdate();

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                }
                e.printStackTrace();
            }
        }
    }

    public GetBannedDto getBanned(long userId) {
        try (Session session = sessionFactory.openSession()) {
            boolean banned = session.createQuery(
                    "select u.banned " +
                            "from User u " +
                            "where u.id = :userId",
                            Boolean.class
                    )
                    .setParameter("userId", userId)
                    .uniqueResult();
            return new GetBannedDto(userId, banned);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String subscribe(long subscriberId, String nickname) {
        try (Session session = sessionFactory.openSession()) {
            User subscriber = session.get(User.class, subscriberId);

            User userToSubscribe = session.createQuery("from User u where u.nickname = :nickname", User.class)
                    .setParameter("nickname", nickname).uniqueResult();

            if (subscriber == null || userToSubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId().equals(userToSubscribe.getId())) {
                return "Cannot subscribe to yourself!";
            }

            Transaction transaction = session.beginTransaction();
            try {
                Long exists = session.createNativeQuery(
                                "select count(*) " +
                                        "from user_subscribes " +
                                        "where user_id = :userId " +
                                        "and subscribed_user_id = :targetId",
                                Long.class
                        )
                        .setParameter("userId", subscriber.getId())
                        .setParameter("targetId", userToSubscribe.getId())
                        .getSingleResult();

                if (exists != null && exists > 0) {
                    transaction.rollback();
                    return "User is already subscribed!";
                }

                session.createNativeQuery(
                                "insert into user_subscribes (user_id, subscribed_user_id) " +
                                        "values (:userId, :targetId)",
                                Long.class
                        )
                        .setParameter("userId", subscriber.getId())
                        .setParameter("targetId", userToSubscribe.getId())
                        .executeUpdate();

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

            User userToUnsubscribe = session.createQuery("from User where nickname = :nickname", User.class)
                    .setParameter("nickname", nickname).uniqueResult();

            if (subscriber == null || userToUnsubscribe == null) {
                return "User not found!";
            }

            if (subscriber.getId().equals(userToUnsubscribe.getId())) {
                return "Cannot unsubscribe from yourself!";
            }

            Transaction transaction = session.beginTransaction();
            try {
                Long exists = session.createNativeQuery(
                                "select count(*) " +
                                        "from user_subscribes " +
                                        "where user_id = :userId " +
                                        "and subscribed_user_id = :targetId",
                                Long.class
                        )
                        .setParameter("userId", subscriber.getId())
                        .setParameter("targetId", userToUnsubscribe.getId())
                        .getSingleResult();

                if (exists == null || exists == 0) {
                    transaction.rollback();
                    return "User is already unsubscribed!";
                }

                session.createNativeQuery(
                                "delete from user_subscribes " +
                                        "where user_id = :userId and " +
                                        "subscribed_user_id = :targetId",
                                        Long.class
                        )
                        .setParameter("userId", subscriber.getId())
                        .setParameter("targetId", userToUnsubscribe.getId())
                        .executeUpdate();

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

    public boolean addFriend(long userId, long friendId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Long existingCount = session.createNativeQuery(
                                "select count(*) from user_friends " +
                                        "where (user_id = :userId and friend_id = :friendId) " +
                                        "or (user_id = :friendId and friend_id = :userId)",
                                Long.class
                        )
                        .setParameter("userId", userId)
                        .setParameter("friendId", friendId)
                        .getSingleResult();

                if (existingCount != null && existingCount > 0) {
                    transaction.rollback();
                    return false;
                }

                User friend = findById(friendId);

                if (friend == null || friend.getId().equals(userId)) {
                    transaction.rollback();
                    return false;
                }

                int addedFriends = session.createNativeMutationQuery(
                                "insert into user_friends(user_id, friend_id) " +
                                        "values (:userId, :friendId) " +
                                        "on conflict (user_id, friend_id) do nothing"
                        )
                        .setParameter("userId", userId)
                        .setParameter("friendId", friendId)
                        .executeUpdate();

                if (addedFriends <= 0) {
                    transaction.rollback();
                    return false;
                }

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

    public boolean removeFriend(long userId, long friendId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User friend = findById(friendId);

                if (friend == null || friend.getId().equals(userId)) {
                    transaction.rollback();
                    return false;
                }

                int deletedFriends = session.createNativeMutationQuery(
                                "delete from user_friends " +
                                        "where (user_id = :userId and friend_id = :friendId) " +
                                        "or (user_id = :friendId and friend_id = :userId)"
                        )
                        .setParameter("userId", userId)
                        .setParameter("friendId", friendId)
                        .executeUpdate();

                if (deletedFriends <= 0) {
                    transaction.rollback();
                    return false;
                }

                transaction.commit();
                return true;
            }  catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    private Map<Long, FriendFlags> findFriendsInfo(long userId) {
        try (Session session = sessionFactory.openSession()) {
            var check1 = session.createNativeQuery(
                            "select friend_id, is_user_confirmed, is_friend_confirmed " +
                                    "from user_friends " +
                                    "where user_id = :userId",
                            Object[].class
                    )
                    .setParameter("userId", userId)
                    .getResultList();

            var check2 = session.createNativeQuery(
                    "select user_id, is_user_confirmed, is_friend_confirmed " +
                            "from user_friends " +
                            "where friend_id = :friend_id",
                            Object[].class
                    )
                    .setParameter("friend_id", userId)
                    .getResultList();

            return Stream.concat(check1.stream(), check2.stream())
                    .collect(Collectors.toMap(
                            row -> ((Number) row[0]).longValue(),
                            row -> new FriendFlags(
                                    Boolean.TRUE.equals(row[1]),
                                    Boolean.TRUE.equals(row[2])
                            ),
                            (oldFlags, newFlags) -> new FriendFlags(
                                    oldFlags.isUserConfirmed() || newFlags.isUserConfirmed(),
                                    oldFlags.isFriendConfirmed() || newFlags.isFriendConfirmed()
                            )
                    ));
        }
    }

    private List<User> findFriendNamesById(List<Long> friendsId) {
        if (friendsId == null || friendsId.isEmpty()) {
            return List.of();
        }

        try (Session session = sessionFactory.openSession()) {
            return session.createNativeQuery(
                            "select u.* " +
                                    "from users u " +
                                    "where u.id in (:friendsId) " +
                                    "and u.banned = false " +
                                    "and u.deleted = false",
                            User.class
                    )
                    .setParameterList("friendsId", friendsId)
                    .getResultList();
        }
    }



    public List<ShowFriendsListDto> showFriendsList(long userId) {
        Map<Long, FriendFlags> friendsInfo = findFriendsInfo(userId);

        if (friendsInfo.isEmpty()) {
            return List.of();
        }

        List<Long> friendsId = friendsInfo.keySet().stream().toList();

        List<User> friends = findFriendNamesById(friendsId);

        return friends.stream()
                .map(f -> {
                    FriendFlags flags = friendsInfo.getOrDefault(
                            f.getId(),
                            new FriendFlags(false, false)
                    );
                    return new ShowFriendsListDto(
                            f.getId(),
                            f.getNickname(),
                            flags.isUserConfirmed(),
                            flags.isFriendConfirmed()
                    );
                })
                .toList();
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
            return -1;
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
            return -1;
        }
    }

    public ViewAccountPageDto viewAccountPage(String nickname) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery(
                            "from User u where u.nickname = :nickname",
                            User.class)
                    .setParameter("nickname", nickname)
                    .uniqueResult();

            if (user == null) {
                return null;
            }

            long subscribers = getSubscribersCount(user.getId());
            long subscriptions = getSubscriptionsCount(user.getId());

            return new ViewAccountPageDto(
                    user.getId(),
                    user.getNickname(),
                    subscribers,
                    subscriptions,
                    user.getBio(),
                    user.getCreatedAt(),
                    user.getLastSeen()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean editAccountPage(long userId, String bio, Instant instant) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                        "update User " +
                                "set bio = :userBio, updatedAt = :instant " +
                                "where id = :userId")
                        .setParameter("userBio", bio)
                        .setParameter("instant", instant)
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

    public boolean newName(String nickname, long userId, Instant instant) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                        "update User " +
                                "set nickname = :nickname, updatedAt = :instant " +
                                "where id = :userId"
                        )
                        .setParameter("nickname", nickname)
                        .setParameter("instant", instant)
                        .setParameter("userId", userId)
                        .executeUpdate();

                transaction.commit();
                return updatedRows > 0;
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                if (e instanceof ConstraintViolationException || e.getCause() instanceof ConstraintViolationException) {
                    return false;
                }
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean newDisplayName(String displayName, long userId, Instant instant) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                                "update User " +
                                        "set displayName = :displayName, updatedAt = :instant " +
                                        "where id = :userId "
                        )
                        .setParameter("displayName", displayName)
                        .setParameter("instant", instant)
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

    public boolean newPass(String incomingHash, long userId, Instant instant) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                User user = session.get(User.class, userId);

                if (user != null) {
                    String bcryptHash = encoder.encode(incomingHash);
                    user.setPassword(bcryptHash);
                    user.setUpdatedAt(instant);

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

    public boolean deleteUser(long userId, String email, String nickname, Instant instant) {
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
                                "set u.deleted = true, " +
                                "u.email = :email, " +
                                "u.nickname = :nickname, " +
                                "u.updatedAt = :instant " +
                                "where u.id = :userId"
                        )
                        .setParameter("userId", userId)
                        .setParameter("email", "deleted_" + userId + email)
                        .setParameter("nickname", "deleted_" + userId + nickname)
                        .setParameter("instant", instant)
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
}