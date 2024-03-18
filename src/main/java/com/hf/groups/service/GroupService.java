package com.hf.groups.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hf.groups.entity.Groups;
import com.hf.groups.entity.UserResponse;
import com.hf.groups.exception.GroupNotFoundException;
import com.hf.groups.exception.UserNotFoundException;
import com.hf.groups.feign.UserFeignClient;
import com.hf.groups.repository.GroupsRepository;

@Service
public class GroupService {

    @Autowired
    private GroupsRepository groupRepository;

    @Autowired
    private UserFeignClient userFeignClient; // Assuming you have a Feign client to fetch user details

    public Groups createGroupAutomatically(Long userId) {
        // Fetch user information to get the associated coach ID
        UserResponse user = userFeignClient.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Long coachId = user.getCoachId();

        // Check if a group with the same coach ID exists
        Groups existingGroup = groupRepository.findByCoachIdAndGroupName(coachId, "Group " + coachId).orElse(null);
        if (existingGroup != null) {
            // If a group exists, add the user to the existing group
            return addUserToGroup(existingGroup.getGroupId(), userId);
        }

        // If no group exists, create a new group and add the user to it
        Groups group = new Groups();
        group.setCoachId(coachId);
        group.setGroupName("Group " + coachId);
        group.setUsers(Collections.singletonList(userId)); // Add the user to the group

        return groupRepository.save(group);
    }
    public Groups addUserAutomatically(Long userId) {
        // Fetch user information to get the associated coach ID
        UserResponse user = userFeignClient.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Long coachId = userFeignClient.getCoachid(userId);


        // Check if a group with the same coach ID exists
        Groups existingGroup = groupRepository.findByCoachIdAndGroupName(coachId, "Group " + coachId).orElse(null);
        if (existingGroup != null) {
            // If a group exists, add the user to the existing group
            return addUserToGroup(existingGroup.getGroupId(), userId);
        }

        // If no group exists, create a new group and add the user to it
        Groups group = new Groups();
        group.setCoachId(coachId);
        group.setGroupName("Group " + coachId);
        group.setUsers(Collections.singletonList(userId)); // Add the user to the group

        return groupRepository.save(group);
    }
    public List<Long> getFriendsUserIds(Long userId) {
        // Fetch friends' user IDs based on the user's group
        Groups group = groupRepository.findByUsersContaining(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found in any group with id: " + userId));

        // Return a list of user IDs excluding the given user's ID
        return group.getUsers().stream().filter(id -> !id.equals(userId)).toList();
    }

  

    public Optional<Groups> getGroupById(Long groupId) {
        return groupRepository.findById(groupId);
    }
    public Groups addUserToGroup(Long groupId, Long userId) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found with id: " + groupId));

        if (!group.getUsers().contains(userId)) {
            group.getUsers().add(userId);
            return groupRepository.save(group);
        }

        return group;
    }
    // Other methods for group-related operations
}
