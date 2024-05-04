package com.hf.groups.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hf.groups.entity.FriendDetailsDTO;
import com.hf.groups.entity.Groups;
import com.hf.groups.entity.UserResponse;
import com.hf.groups.exception.GroupNotFoundException;
import com.hf.groups.exception.UserNotFoundException;
import com.hf.groups.feign.ActivityFeignClient;
import com.hf.groups.feign.UserFeignClient;
import com.hf.groups.repository.GroupsRepository;

@Service
public class GroupService {

    @Autowired
    private GroupsRepository groupRepository;

    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private ActivityFeignClient activityFeignClient;

    public Groups createGroupAutomatically(Long userId) {
        UserResponse user = userFeignClient.getUser(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Long coachId = user.getCoachId();

        Groups existingGroup = groupRepository.findByCoachIdAndGroupName(coachId, "Group " + coachId).orElse(null);
        if (existingGroup != null) {
            return addUserToGroup(existingGroup.getGroupId(), userId);
        }

        Groups group = new Groups();
        group.setCoachId(coachId);
        group.setGroupName("Group " + coachId);
        group.setUsers(Collections.singletonList(userId));

        return groupRepository.save(group);
    }

    public List<FriendDetailsDTO> getFriendsInfo(Long userId) {
        // Fetch the group information for the given user
        Groups group = groupRepository.findByUsersContaining(userId)
                .orElseThrow(() -> new GroupNotFoundException("User not found in any group with id: " + userId));

        // Fetch information about the users in the group
        List<Long> userIds = group.getUsers();
        List<FriendDetailsDTO>friendsInfo = new ArrayList<>();

        // Fetch information about each user using Feign clients
        for (Long friendId : userIds) {
            // Fetch user information
            UserResponse user = userFeignClient.getUser(friendId);
            if (user == null) {
                throw new UserNotFoundException("User not found with id: " + friendId);
            }

            // Fetch total steps walked by the user
            Long totalSteps = activityFeignClient.getTotalSteps(friendId);

            // Create FriendDetailsDto object and add it to the list
            FriendDetailsDTO friendDetailsDto = new FriendDetailsDTO();
            friendDetailsDto.setUserId(friendId);
            friendDetailsDto.setUsername(user.getUsername());
            friendDetailsDto.setTotalSteps(totalSteps);
            friendsInfo.add(friendDetailsDto);
        }

        return friendsInfo;
    }

    public List<Long> getTotalSteps(List<Long> userIds) {
        return userIds.stream()
                .map(activityFeignClient::getTotalSteps)
                .toList();
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
}
