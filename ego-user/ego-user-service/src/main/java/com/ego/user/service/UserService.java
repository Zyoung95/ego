package com.ego.user.service;

import com.ego.user.pojo.User;

public interface UserService {
    Boolean check(String data, Integer type);

    Boolean send(String phone);

    Boolean register(User user, String code);

    User query(String username, String password);
}
