package com.ego.user.service.impl;

import com.ego.commom.utils.CodecUtils;
import com.ego.commom.utils.NumberUtils;
import com.ego.user.mapper.UserMapper;
import com.ego.user.pojo.User;
import com.ego.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private static final String PREFIX = "user:sms:phone:";

    @Override
    public Boolean check(String data, Integer type) {
        if(type!=null){
            User user = new User();
            if(type==1){
                user.setUsername(data);
            }else if(type==2){
                user.setPhone(data);
            }
            List<User> select = userMapper.select(user);
            if(select==null||select.size()==0){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean send(String phone) {
        Map map = new HashMap();
        String code = NumberUtils.generateCode(6);
        map.put("phone",phone);
        map.put("code",code);
        amqpTemplate.convertAndSend("user.code",map);
        stringRedisTemplate.opsForValue().set(PREFIX+phone,code,3, TimeUnit.MINUTES);
        return true;
    }

    @Transactional
    @Override
    public Boolean register(User user, String code) {
        if(StringUtils.isNotEmpty(code)){
            String key = PREFIX + user.getPhone();
            String oldCode = stringRedisTemplate.opsForValue().get(key);
            if(code.equals(oldCode)){
                String encode = CodecUtils.passwordBcryptEncode(user.getUsername(), user.getPassword());
                user.setPassword(encode);
                user.setCreated(new Date());
                userMapper.insert(user);
                stringRedisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }

    @Override
    public User query(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user = userMapper.selectOne(user);
        if(user!=null){
            Boolean isOk = CodecUtils.passwordConfirm(username+password, user.getPassword());
            if(isOk){
                return user;
            }
        }
        return null;
    }
}
