package org.crudfeed.core.repository;

import org.crudfeed.core.dto.ViewFeedRequestDto;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.crudfeed.core.entity.Post;
import org.crudfeed.core.entity.User;
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

    public List<ViewFeedRequestDto> viewFeed() {
        try (Session session = sessionFactory.openSession()) {
            List<Post> posts = session.createQuery(
                    "from Post p " +
                            "where p.deleted = false " +
                            "and banned = false " +
                            "order by p.createdAt asc",
                    Post.class
            ).getResultList();

            return posts.stream()
                    .map(post -> new ViewFeedRequestDto(
                            post.getId(),
                            post.getUserId(),
                            userRepository.getNicknameById(post.getUserId()),
                            post.getContent(),
                            getLikesCount(session, post.getId()),
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

// TODO: REWORK POST REPO

//    public List<Post> showUserPostsById(long userId) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "SELECT p\n" +
//                                    "FROM Post p\n" +
//                                    "JOIN p.likedByUsers u\n" +
//                                    "WHERE u.id = :userId",
//                            Post.class)
//                    .setParameter("userId", userId)
//                    .getResultList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }
//
//    public List<Post> showUserLikes(long userId) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "SELECT p FROM Post p " +
//                                    "JOIN FETCH p.user " +
//                                    "WHERE p.user.id = :userId",
//                            Post.class)
//                    .setParameter("userId", userId)
//                    .getResultList();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }
//
//    public Post searchPostById(long id) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "SELECT p FROM Post p " +
//                                    "JOIN FETCH p.user " +
//                                    "WHERE p.id = :id",
//                            Post.class)
//                    .setParameter("id", id)
//                    .uniqueResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public List<Post> searchPostByWords(String keyword) {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery(
//                            "FROM Post p " +
//                                    "JOIN FETCH p.user " +
//                                    "WHERE lower(p.text) " +
//                                    "LIKE lower(:word) " +
//                                    "ORDER BY p.createdAt ASC",
//                            Post.class)
//
//                    .setParameter("word", "%" + keyword + "%")
//                    .getResultList();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return List.of();
//        }
//    }

    public String likePost(long postId, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Post post = session.get(Post.class, postId);
            User user = session.get(User.class, userId);

            if (post == null || user == null) {
                return "Post not found!";
            }

//            if (post.getLikedByUsers().contains(user)) {
//                return "Post already liked!";
//            }
//
//            post.getLikedByUsers().add(user);
//            post.setLikes(post.getLikes() + 1);

            session.merge(post);
            transaction.commit();

            return "Post successfully liked!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during like process!";
        }
    }

    public String dislikePost(long postId, long userId) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Post post = session.get(Post.class, postId);
            User user = session.get(User.class, userId);

            if (post == null || user == null) {
                return "Post not found!";
            }

//            if (!post.getLikedByUsers().contains(user)) {
//                return "You have not liked this post yet!";
//            }
//
//            post.getLikedByUsers().remove(user);
//            post.setLikes(Math.max(0, post.getLikes() - 1));

            session.merge(post);
            transaction.commit();

            return "Like removed!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error during dislike process!";
        }
    }
}