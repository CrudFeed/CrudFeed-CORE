package org.crudfeed.core.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {

    @Getter
    private static final Session INSTANCE = new Session();

    @Getter
    private User currentUser;

    @Getter
    private final long guestId = java.util.concurrent.ThreadLocalRandom.current().nextLong(2000000000);

    @Getter @Setter
    private boolean exit = false;

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot login with null user!");
        }
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }
}