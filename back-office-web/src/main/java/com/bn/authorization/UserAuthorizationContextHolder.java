package com.bn.authorization;

import org.springframework.core.NamedThreadLocal;

public class UserAuthorizationContextHolder {
    private static final ThreadLocal<UserAuthorization> CONTEXT = new NamedThreadLocal<>("User Authorization");

    public static void set(UserAuthorization auth) {
        CONTEXT.set(auth);
    }

    public static UserAuthorization get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
