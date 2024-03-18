package com.hf.groups.feign;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hf.groups.entity.UserResponse;

@FeignClient(name = "HFUSER1", url = "http://localhost:8081") // Update with the actual URL
public interface UserFeignClient {

    @GetMapping(value= "/api/users/{userId}",produces = "application/json")
    UserResponse getUser(@PathVariable Long userId);
    
    @GetMapping(value="api/users/getcoachidinfo/{userId}",produces = "application/json")
    Long getCoachid(@PathVariable Long userId);

   
}