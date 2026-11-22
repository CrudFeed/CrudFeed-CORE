package org.crudfeed.core.entity;

public enum Role {
    USER,
    MODERATOR,
    ADMIN,
    OWNER;

    public boolean hasAtLeast(Role required) {
        return this.ordinal() >= required.ordinal();
    }
}