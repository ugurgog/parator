package com.paypad.parator.eventBusModel;

import com.paypad.parator.model.User;

public class UserBus {

    User user;

    public UserBus(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
