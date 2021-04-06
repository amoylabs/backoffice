package com.bn.web.authorization;

public final class UserAuthorization {
    static UserAuthorization valid(UserRealm auth) {
        return new UserAuthorization(auth);
    }

    static UserAuthorization invalid(String message) {
        return new UserAuthorization(message);
    }

    private final Boolean valid;
    private UserRealm userRealm;
    private String failureMsg;

    private UserAuthorization(UserRealm userRealm) {
        this.valid = Boolean.TRUE;
        this.userRealm = userRealm;
    }

    private UserAuthorization(String failureMsg) {
        this.valid = Boolean.FALSE;
        this.failureMsg = failureMsg;
    }

    public Boolean isValid() {
        return valid;
    }

    public UserRealm getUserRealm() {
        return userRealm;
    }

    public String getFailureMessage() {
        return failureMsg;
    }
}
