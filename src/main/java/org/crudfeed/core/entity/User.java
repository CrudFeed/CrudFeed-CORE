package org.crudfeed.core.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable =false)
    private String nickname;

    @Column(name = "display_name", nullable =false)
    private String displayName;

    @Column(nullable = false)
    private String password;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "last_seen")
    private OffsetDateTime lastSeen;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    private boolean banned;

    @Column(name = "is_verified_email")
    private boolean isVerifiedEmail;

    private boolean deleted;

    @ManyToMany
    @JoinTable(
            name = "user_subscribes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_user_id")
    )
    private Set<User> subscriptions = new HashSet<>();
}