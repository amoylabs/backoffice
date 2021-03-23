package com.bn.authorization;

public final class JWTVerificationResult {
    static JWTVerificationResult pass(UserAuthorization auth) {
        return new JWTVerificationResult(auth);
    }

    static JWTVerificationResult fail(String message) {
        return new JWTVerificationResult(message);
    }

    private final Boolean passed;
    private UserAuthorization auth;
    private String failureMsg;

    private JWTVerificationResult(UserAuthorization auth) {
        this.passed = Boolean.TRUE;
        this.auth = auth;
    }

    private JWTVerificationResult(String failureMsg) {
        this.passed = Boolean.FALSE;
        this.failureMsg = failureMsg;
    }

    Boolean isPassed() {
        return passed;
    }

    UserAuthorization getAuth() {
        return auth;
    }

    String getFailureMessage() {
        return failureMsg;
    }
}
