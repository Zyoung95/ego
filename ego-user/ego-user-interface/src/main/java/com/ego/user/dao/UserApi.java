package com.ego.user.dao;

import com.ego.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface UserApi {
    @GetMapping("/query")
    public ResponseEntity<User> query(
            @RequestParam("username") String username,
            @RequestParam("password") String password);
}
