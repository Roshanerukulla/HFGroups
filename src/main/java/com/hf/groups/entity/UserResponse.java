package com.hf.groups.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class UserResponse {

    private Long userId;

    private Long coachId;

    // other fields and getters and setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }
}
