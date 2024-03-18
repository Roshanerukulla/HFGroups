package com.hf.groups.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hf.groups.entity.Groups;
import com.hf.groups.entity.UserResponse;
import com.hf.groups.exception.GroupAlreadyExistException;
import com.hf.groups.exception.GroupNotFoundException;
import com.hf.groups.exception.UserNotFoundException;
import com.hf.groups.feign.UserFeignClient;
import com.hf.groups.service.GroupService;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserFeignClient userFeignClient;

    @PostMapping("/create-auto")
    public ResponseEntity<Groups> createGroupAutomatically(@RequestParam Long userId) {
        try {
            // Fetch user information to get the associated coach ID
            UserResponse user = userFeignClient.getUser(userId);
            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }

            // Use the coach ID to create or retrieve the group
            Groups createdGroup = groupService.createGroupAutomatically(userId);
            return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
        } catch (UserNotFoundException | GroupAlreadyExistException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{groupId}/add-user")
    public ResponseEntity<Groups> addUserToGroup(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            Groups updatedGroup = groupService.addUserToGroup(groupId, userId);
            return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
        } catch (GroupNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/add-user-auto")
    public ResponseEntity<Groups> addUserAutomatically(@RequestParam Long userId) {
        try {
            Groups updatedGroup = groupService.addUserAutomatically(userId);
            return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
        } catch (UserNotFoundException | GroupNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/friends/{userId}")
    public ResponseEntity<List<Long>> getFriendsUserIds(@PathVariable Long userId) {
        List<Long> friendsUserIds = groupService.getFriendsUserIds(userId);
        return ResponseEntity.ok(friendsUserIds);
    }



    // Other endpoints for group-related operations

    // Example endpoint to get group information by ID
    @GetMapping("/{groupId}")
    public ResponseEntity<Groups> getGroupById(@PathVariable Long groupId) {
        Optional<Groups> group = groupService.getGroupById(groupId);
        return group.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
