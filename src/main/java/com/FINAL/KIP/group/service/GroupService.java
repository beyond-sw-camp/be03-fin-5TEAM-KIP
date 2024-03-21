package com.FINAL.KIP.group.service;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import com.FINAL.KIP.group.dto.req.CreateGroupReqDto;
import com.FINAL.KIP.group.dto.req.addUsersToGroupReqDto;
import com.FINAL.KIP.group.dto.res.GroupResDto;
import com.FINAL.KIP.group.dto.res.GetGroupHierarchyResDto;
import com.FINAL.KIP.group.dto.res.GroupUsersRoleResDto;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final UserService userService;
    private final GroupRepository groupRepo;
    private final GroupUserRepository groupUserRepo;

    @Autowired
    public GroupService(GroupRepository groupRepo, UserService userService, GroupUserRepository groupUserRepo) {
        this.userService = userService;
        this.groupRepo = groupRepo;
        this.groupUserRepo = groupUserRepo;
    }


    //    Create
    public GroupResDto createGroup(CreateGroupReqDto dto) {
        Group newGroup = createNewGroup(dto);
        return new GroupResDto(groupRepo.save(newGroup));
    }

    public List<GroupResDto> createGroups(List<CreateGroupReqDto> dtos) {
        return dtos.stream()
                .map(this::createGroup)
                .collect(Collectors.toList());
    }

    public List<GroupUsersRoleResDto> addUsersToGroup(addUsersToGroupReqDto dto) {
        List<GroupUser> addedUsers = getGroupUsers(dto);
        return addedUsers.stream()
                .map(GroupUsersRoleResDto::new)
                .collect(Collectors.toList());
    }

    public List<List<GroupUsersRoleResDto>> addUsersToGroupList(List<addUsersToGroupReqDto> dtos) {
        List<List<GroupUsersRoleResDto>> addResult = new ArrayList<>();
        for (addUsersToGroupReqDto dto : dtos)
            addResult.add(addUsersToGroup(dto));
        return addResult;
    }


    //    Read
    public GetGroupHierarchyResDto getGroupHierarchy(Long groupId) {
        return new GetGroupHierarchyResDto(getGroupById(groupId));
    }

    public List<GroupUsersRoleResDto> getGroupUsers(Long groupId) {
        Group group = getGroupById(groupId);
        return getByGroup(group).stream()
                .map(GroupUsersRoleResDto::new)
                .collect(Collectors.toList());
    }

    public List<GroupResDto> getGroupsById(Long groupId) {
        return getGroupById(groupId).getChildGroups().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

    //    함수 공통화
    public Group getGroupById(Long supperGroupId) {
        return groupRepo.findById(supperGroupId).orElseThrow(EntityNotFoundException::new);
    }


    public Group createNewGroup(CreateGroupReqDto dto) {
        Group newGroup = dto.makeAuthorityReqDtoToGroup();

        Optional.ofNullable(dto.getSupperGroupId())
                .map(this::getGroupById)
                .ifPresent(newGroup::setSuperGroup);

        return newGroup;
    }

    public List<GroupUser> getGroupUsers(addUsersToGroupReqDto dto) {
        List<GroupUser> addedUsers = new ArrayList<>();
        Group group = getGroupById(dto.getGroupId());
        for (UserIdAndGroupRole user : dto.getGroupUsers()) {
            User tempUser = userService.getUserById(user.getUserId());
            GroupUser newGroupUser = GroupUser.builder()
                    .group(group)
                    .user(tempUser)
                    .groupRole(user.getGroupRole())
                    .build();
            addedUsers.add(groupUserRepo.save(newGroupUser));
        }
        return addedUsers;
    }

    public List<GroupUser> getByGroup(Group group) {
        return groupUserRepo.findByGroup(group);
    }


    public List<UserIdAndGroupRole> getAccessibleUsers(Long groupId) {
        List<UserIdAndGroupRole> accessibleUsers = new ArrayList<>();
        List<Long> groupIdList = new ArrayList<>();

        Group group = getGroupById(groupId);

        do {
            groupIdList.add(group.getId());
            group = group.getSuperGroup();
        } while (group.getSuperGroup() != null);
        groupIdList.add(group.getId());

        for(Long superGroupId : groupIdList)
            System.out.println("상위 그룹 아이디" + superGroupId);

        for(Long superGroupId : groupIdList) {
            Group superGroup = getGroupById(superGroupId);
            List<GroupUser> superGroupUsers = getByGroup(superGroup);
            for (GroupUser groupUser : superGroupUsers) {
                UserIdAndGroupRole userIdAndGroupRole = new UserIdAndGroupRole();
                userIdAndGroupRole.setUserId(groupUser.getUser().getId());
                userIdAndGroupRole.setGroupRole(groupUser.getGroupRole().name());
                accessibleUsers.add(userIdAndGroupRole);
            }
        }
        return accessibleUsers;
    }

    public List<GroupResDto> getGroups() {
        return groupRepo.findAll().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

}
