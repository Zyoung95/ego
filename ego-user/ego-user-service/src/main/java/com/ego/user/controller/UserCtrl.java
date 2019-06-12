package com.ego.user.controller;


import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserCtrl {

    @Autowired
    private UserService userService;
    //http://api.ego.com/api/user/check/ZCYoung0219/1
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> check(@PathVariable("data") String data,@PathVariable("type") Integer type){
        Boolean isOk = userService.check(data,type);
        if(isOk==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(isOk);
    }

    @PostMapping("/send")
    public ResponseEntity<Void> send(@RequestParam("phone") String phone){
        Boolean isOk = userService.send(phone);
        if(!isOk){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }
    //http://api.ego.com/api/user/register
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code){
        Boolean isOk = userService.register(user,code);
        if(!isOk){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @GetMapping("/query")
    public ResponseEntity<User> query(@RequestParam("username") String username,@RequestParam("password") String password){
        User user = userService.query(username,password);
        if(user==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(user);
    }
}
