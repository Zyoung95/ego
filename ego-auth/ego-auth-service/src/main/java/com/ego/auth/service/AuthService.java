package com.ego.auth.service;

import com.ego.auth.client.AuthClient;
import com.ego.auth.config.JwtProperties;
import com.ego.auth.entity.UserInfo;
import com.ego.auth.utils.JwtUtils;
import com.ego.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;

@Service
//@EnableConfigurationProperties(JwtProperties.class)
public class AuthService {
    @Autowired
    private AuthClient authClient;

   /* @Autowired
    private JwtProperties jwtProperties;*/
    public String query(String username, String password, PrivateKey privateKey, int expire) {
        User user = authClient.query(username, password).getBody();
        try {
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),privateKey,expire /*jwtProperties.getPrivateKey(), jwtProperties.getExpire()*/);
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
