package org.crudfeed.core.repository;

import org.crudfeed.core.dto.mod.ModRoleInfoResponseDto;
import org.crudfeed.core.dto.mod.ModShowResponseDto;
import org.crudfeed.core.entity.Role;
import org.crudfeed.core.entity.User;
import org.crudfeed.core.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class ModRepository {

    private final SessionFactory sessionFactory;

    public ModRepository() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
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

    public boolean modDeleteOrRestore(long targetId, String action) {
        if (targetId <= 0 || action == null) {
            return false;
        }

        String hql = switch (action.toLowerCase()) {
            case "account-delete" -> "update User u set u.deleted = true where u.id = :targetId and u.deleted = false";
            case "post-delete" -> "update Post p set p.deleted = true where p.id = :targetId and p.deleted = false";

            case "account-restore" -> "update User u set u.deleted = false where u.id = :targetId and u.deleted = true";
            case "post-restore" -> "update Post p set p.deleted = false where p.id = :targetId and p.deleted = true";

            default -> null;
        };


        if (hql == null) {
            return false;
        }

        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updated = session.createMutationQuery(hql)
                        .setParameter("targetId", targetId)
                        .executeUpdate();

                if (updated == 0) {
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

    public ModRoleInfoResponseDto modChangeRoleForAccount(long userId, Role role) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int promotedUsers = session.createMutationQuery(
                                "update User u " +
                                        "set u.role =:role " +
                                        "where u.id =:userId"
                        )
                        .setParameter("role", role)
                        .setParameter("userId", userId)
                        .executeUpdate();

                if (promotedUsers == 0) {
                    transaction.rollback();
                    return null;
                }

                transaction.commit();
                return new ModRoleInfoResponseDto(role);
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return null;
            }
        }
    }

    public ModShowResponseDto modShow(long targetId) {
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery(
                            "from User u where u.id = :targetId",
                            User.class)
                    .setParameter("targetId", targetId)
                    .uniqueResult();

            if (user == null) {
                return null;
            }

                return new ModShowResponseDto(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getDisplayName(),
                        user.getBio(),
                        user.getRole(),
                        user.getAvatarUrl(),
                        user.getLastSeen(),
                        user.getCreatedAt(),
                        user.getUpdatedAt(),
                        user.isVerifiedEmail(),
                        user.isBanned(),
                        user.isDeleted()
                        );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
