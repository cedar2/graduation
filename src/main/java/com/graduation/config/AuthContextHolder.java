package com.graduation.config;

import com.graduation.common.AuthUser;

public final class AuthContextHolder {

    private static final ThreadLocal<AuthUser> CONTEXT = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void set(AuthUser authUser) {
        CONTEXT.set(authUser);
    }

    public static AuthUser get() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}

