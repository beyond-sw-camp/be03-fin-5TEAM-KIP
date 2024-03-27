package com.FINAL.KIP.group.service;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import com.FINAL.KIP.group.dto.req.CreateGroupReqDto;
import com.FINAL.KIP.group.dto.req.addUsersToGroupReqDto;
import com.FINAL.KIP.group.dto.res.GetGroupHierarchyResDto;
import com.FINAL.KIP.group.dto.res.GroupResDto;
import com.FINAL.KIP.group.dto.res.GroupUsersResDto;
import com.FINAL.KIP.group.dto.res.GroupUsersRoleResDto;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public GroupService(GroupRepository groupRepo, UserService userService,
                        GroupUserRepository groupUserRepo) {
        this.userService = userService;
        this.groupRepo = groupRepo;
        this.groupUserRepo = groupUserRepo;
    }

    //    Create
    @Transactional
    @PreAuthorize("hasAuthority('ADMIN')")
    public GroupResDto createGroup(CreateGroupReqDto dto) {
        Group newGroup = createNewGroup(dto);
        Group savedNewGroup = groupRepo.save(newGroup);
        savedNewGroup.getDocuments().get(0).setTitle(newGroup.getGroupName() + " 그룹에 오신것을 환영합니다.");
        return new GroupResDto(savedNewGroup);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Transactional
    public List<GroupResDto> createGroups(List<CreateGroupReqDto> dtos) {
        return dtos.stream()
                .map(this::createGroup)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<GroupUsersRoleResDto> addUsersToGroup(addUsersToGroupReqDto dto) {
        List<GroupUser> addedUsers = getGroupUsers(dto);
        return addedUsers.stream()
                .map(GroupUsersRoleResDto::new)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<List<GroupUsersRoleResDto>> addUsersToGroupList(List<addUsersToGroupReqDto> dtos) {
        List<List<GroupUsersRoleResDto>> addResult = new ArrayList<>();
        for (addUsersToGroupReqDto dto : dtos)
            addResult.add(addUsersToGroup(dto));
        return addResult;
    }


    //    Read
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public GetGroupHierarchyResDto getGroupHierarchy(Long groupId) {
        return new GetGroupHierarchyResDto(getGroupById(groupId));
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public GroupUsersResDto getGroupUsers(Long groupId) {
        Group group = getGroupById(groupId);
        List<GroupUser> groupUsers = getByGroup(group);
        return new GroupUsersResDto(groupUsers);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<GroupResDto> getGroupsById(Long groupId) {
        return getGroupById(groupId).getChildGroups().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

    //    함수 공통화
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public Group getGroupById(Long supperGroupId) {
        return groupRepo.findById(supperGroupId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "그룹 아이디로 검색할 수 있는 그룹이 없습니다. " + supperGroupId));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public Group createNewGroup(CreateGroupReqDto dto) {
        Group newGroup = dto.makeAuthorityReqDtoToGroup();
        Optional.ofNullable(dto.getSupperGroupId())
                .map(this::getGroupById)
                .ifPresent(newGroup::setSuperGroup);
        return newGroup;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<GroupUser> getByGroup(Group group) {
        return groupUserRepo.findByGroup(group);
    }
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<UserIdAndGroupRole> getAccessibleUsers(Long groupId) {
        List<UserIdAndGroupRole> accessibleUsers = new ArrayList<>();
        Group myTeamGroup = getGroupById(groupId);
        List<GroupUser> groupUsers = getByGroup(myTeamGroup);
        for (GroupUser groupUser : groupUsers) {
            UserIdAndGroupRole userIdAndGroupRole = new UserIdAndGroupRole();
            if (groupUser.getUser() != null) {
                userIdAndGroupRole.setUserId(groupUser.getUser().getId());
                userIdAndGroupRole.setGroupRole(groupUser.getGroupRole().name());
                accessibleUsers.add(userIdAndGroupRole);
            }
        }
        return accessibleUsers;
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public List<GroupResDto> getGroups() {
        return groupRepo.findAll().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

}
