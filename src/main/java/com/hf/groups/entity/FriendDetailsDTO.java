package com.hf.groups.entity;

public class FriendDetailsDTO {
    private Long userId;
    private String username;
    private Long totalSteps;

    // Default constructor
    public FriendDetailsDTO() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(Long totalSteps) {
        this.totalSteps = totalSteps;
    }

    public FriendDetailsDTO(Long userId, String username, Long totalSteps) {
        this.userId = userId;
        this.username = username;
        this.totalSteps = totalSteps;
    }
}
