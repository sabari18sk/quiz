package com.quizplatform.enums;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED;

    public boolean isActive() {
        return this == ACTIVE;
    }
}
