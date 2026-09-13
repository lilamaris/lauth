package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.User;

public interface UserStore {
    void save(User user);
}
