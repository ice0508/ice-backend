package com.ou.context;

import com.ou.pojo.UserMessage;

public class UserContext {

    private static final ThreadLocal<UserMessage> USER = new ThreadLocal<>();

    public static void setUser(UserMessage user) {
        USER.set(user);
    }

    public static UserMessage getUser() {
        return USER.get();
    }

    public static void removeUser() {
        USER.remove();
    }

    public static Integer getUserId() {
        UserMessage user = USER.get();
        return user != null ? user.getId() : null;
    }

    public static String getUsername() {
        UserMessage user = USER.get();
        return user != null ? user.getUsername() : null;
    }

    public static String getRole() {
        UserMessage user = USER.get();
        return user != null ? user.getIdentity().getDescription() : null;
    }
}