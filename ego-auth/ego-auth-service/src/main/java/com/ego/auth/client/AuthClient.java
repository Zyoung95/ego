package com.ego.auth.client;

import com.ego.user.dao.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("user-service")
public interface AuthClient extends UserApi {
}
