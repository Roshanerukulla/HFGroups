package com.hf.groups.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "HFActivity", url = "http://localhost:8089") // Update URL as per your Activity microservice URL
public interface ActivityFeignClient {

    @GetMapping("/api/activities/{userId}/total-steps")
    Long getTotalSteps(@PathVariable Long userId);
}
