package org.crudfeed.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable =false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    private String bio;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_subscribes",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "subscribed_user_id")
    )
    private Set<User> subscriptions = new HashSet<>();

    @ManyToMany(mappedBy = "subscriptions")
    private Set<User> subscribers = new HashSet<>();

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public User(String nickname, String password, long id) {
        this.id = id;
        this.nickname = nickname;
        this.password = password;
    }
}