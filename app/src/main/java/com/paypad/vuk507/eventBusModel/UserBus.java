package com.paypad.vuk507.eventBusModel;

import com.paypad.vuk507.model.User;

public class UserBus {

    User user;

    public UserBus(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
