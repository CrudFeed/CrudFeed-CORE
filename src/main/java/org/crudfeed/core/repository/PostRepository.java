package org.crudfeed.core.repository;

import org.crudfeed.core.dto.post.ViewPostRequestDto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.crudfeed.core.entity.Post;
import org.crudfeed.core.util.HibernateUtil;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    private final SessionFactory sessionFactory;
    private final UserRepository userRepository;

    public PostRepository(UserRepository userRepository) {
        this.sessionFactory = HibernateUtil.getSessionFactory();
        this.userRepository = userRepository;
    }

    public boolean createPost(Post post) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(post);
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

    public boolean updatePost(long postId, String content, long user_id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                int updatedRows = session.createMutationQuery(
                                "update Post set content = :content where id = :postId and userId = :userId"
                        )
                        .setParameter("content", content)
                        .setParameter("postId", postId)
                        .setParameter("userId", user_id)
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

    public boolean deletePost(long postId, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.createMutationQuery(
                        "update Post p " +
                                "set deleted = true " +
                                "where p.id = :postId " +
                                "and p.userId = :userId"
                        )
                        .setParameter("postId", postId)
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

    public List<ViewPostRequestDto> viewFeed() {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createQuery(
                    "from Post p " +
                            "where p.deleted = false " +
                            "and banned = false " +
                            "order by p.createdAt asc",
                    Post.class
            ).getResultList();

            return posts.stream()
                    .map(post -> new ViewPostRequestDto(
                            post.getId(),
                            post.getUserId(),
                            userRepository.getNicknameById(post.getUserId()),
                            post.getContent(),
                            getLikesCount(session, post.getId()),
                            getDislikesCount(session, post.getId()),
                            post.getCreatedAt(),
                            post.getUpdatedAt()
                    ))
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private long getLikesCount(Session session, long postId) {
        Long count = session.createNativeQuery(
                        "select count(*) from user_liked_posts where post_id = :postId",
                        Long.class
                )
                .setParameter("postId", postId)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    private long getDislikesCount(Session session, long postId) {
        Long count = session.createNativeQuery(
                        "select count(*) from user_disliked_posts where post_id = :postId",
                        Long.class
                )
                .setParameter("postId", postId)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    public Post findById(long postId) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Post p " + "where p.id = :postId ", Post.class)
                    .setParameter("postId", postId)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ViewPostRequestDto> showUserPostsById(long userId) {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createQuery(
                    "from Post p " +
                            "where p.userId = :userId",
                            Post.class)
                    .setParameter("userId", userId)
                    .getResultList();

            return posts.stream()
                    .map(p -> new ViewPostRequestDto(
                            p.getId(),
                            p.getUserId(),
                            userRepository.getNicknameById(p.getUserId()),
                            p.getContent(),
                            getLikesCount(session, p.getId()),
                            getDislikesCount(session, p.getId()),
                            p.getCreatedAt(),
                            p.getUpdatedAt()
                    ))
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public List<ViewPostRequestDto> showUserLikes(long userId) {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createNativeQuery(
                    "select user_id from user_liked_posts " +
                            "where user_id = :userId",
                    Post.class
                    )
                    .setParameter("userId", userId)
                    .getResultList();

            return posts.stream()
                    .map(p -> new ViewPostRequestDto(
                            p.getId(),
                            p.getUserId(),
                            userRepository.getNicknameById(p.getUserId()),
                            p.getContent(),
                            getLikesCount(session, p.getId()),
                            getDislikesCount(session, p.getId()),
                            p.getCreatedAt(),
                            p.getUpdatedAt()
                    ))
                    .toList();

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public ViewPostRequestDto searchPostById(long postId) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.createQuery(
                    "from Post p where p.id = :postId",
                            Post.class)
                    .setParameter("postId", postId)
                    .uniqueResult();

            if (post == null) {
                return null;
            }

            return new ViewPostRequestDto(
                    post.getId(),
                    post.getUserId(),
                    userRepository.getNicknameById(post.getUserId()),
                    post.getContent(),
                    getLikesCount(session, post.getId()),
                    getDislikesCount(session, post.getId()),
                    post.getCreatedAt(),
                    post.getUpdatedAt()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ViewPostRequestDto> searchPostByWords(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createQuery(
                            "from Post p " +
                                    "where banned = false and lower(p.content) " +
                                    "like lower(:word) " +
                                    "order by p.createdAt asc",
                            Post.class)

                    .setParameter("word", "%" + keyword + "%")
                    .getResultList();

            return posts.stream()
                    .map(p -> new ViewPostRequestDto(
                            p.getId(),
                            p.getUserId(),
                            userRepository.getNicknameById(p.getUserId()),
                            p.getContent(),
                            getLikesCount(session, p.getId()),
                            getDislikesCount(session, p.getId()),
                            p.getCreatedAt(),
                            p.getUpdatedAt()

                    ))
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public String likePost(long postId, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (userId <= 0 || postId <= 0) {
                    transaction.rollback();
                    return "Invalid user or post id!";
                }

                int likedPosts = session.createNativeMutationQuery(
                                "insert into user_liked_posts(user_id, post_id) " +
                                        "values (:userId, :postId) " +
                                        "on conflict (user_id, post_id) do nothing"
                        )
                        .setParameter("userId", userId)
                        .setParameter("postId", postId)
                        .executeUpdate();

                if (likedPosts <= 0) {
                    transaction.rollback();
                    return "Post is already liked!";
                }

                int deletedDislikes = session.createNativeMutationQuery(
                                "delete from user_disliked_posts " +
                                        "where user_id = :userId " +
                                        "and post_id = :postId"
                        )
                        .setParameter("userId", userId)
                        .setParameter("postId", postId)
                        .executeUpdate();

                transaction.commit();
                return deletedDislikes > 0
                        ? "Post liked and dislike removed!"
                        : "Post liked!";
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return "Error during like process!";
            }
        }
    }

    public String dislikePost(long postId, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (userId <= 0 || postId <= 0) {
                    transaction.rollback();
                    return "Invalid user or post id!";
                }

                int dislikedPosts = session.createNativeMutationQuery(
                                "insert into user_disliked_posts(user_id, post_id) " +
                                        "values (:userId, :postId) " +
                                        "on conflict (user_id, post_id) do nothing"
                        )
                        .setParameter("userId", userId)
                        .setParameter("postId", postId)
                        .executeUpdate();

                if (dislikedPosts <= 0) {
                    transaction.rollback();
                    return "Post is already disliked!";
                }

                int deletedLikes = session.createNativeMutationQuery(
                                "delete from user_liked_posts " +
                                        "where user_id = :userId " +
                                        "and post_id = :postId"
                        )
                        .setParameter("userId", userId)
                        .setParameter("postId", postId)
                        .executeUpdate();

                transaction.commit();
                return deletedLikes > 0
                        ? "Post disliked and like removed!"
                        : "Post disliked!";
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                e.printStackTrace();
                return "Error during dislike process!";
            }
        }
    }
}