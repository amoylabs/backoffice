package com.bn.web.authorization;

import org.springframework.core.NamedThreadLocal;

public class UserRealmContextHolder {
    private static final ThreadLocal<UserRealm> CONTEXT = new NamedThreadLocal<>("User Realm");

    public static void set(UserRealm auth) {
        CONTEXT.set(auth);
    }

    public static UserRealm get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
