package com.FINAL.KIP.group.service;

import com.FINAL.KIP.common.aspect.JustAdmin;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.group.domain.*;
import com.FINAL.KIP.group.dto.req.CreateGroupReqDto;
import com.FINAL.KIP.group.dto.req.UpdateGroupReqDto;
import com.FINAL.KIP.group.dto.req.addUsersToGroupReqDto;
import com.FINAL.KIP.group.dto.res.GetGroupHierarchyResDto;
import com.FINAL.KIP.group.dto.res.GroupResDto;
import com.FINAL.KIP.group.dto.res.GroupUsersResDto;
import com.FINAL.KIP.group.dto.res.GroupUsersRoleResDto;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.group.repository.GroupUserRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.user.service.UserService;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.repository.VersionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final UserService userService;
    private final GroupRepository groupRepo;
    private final GroupUserRepository groupUserRepo;
    private final UserRepository userRepository;
    private final VersionRepository versionRepository;

    @Autowired
    public GroupService(GroupRepository groupRepo, UserService userService,
                        GroupUserRepository groupUserRepo,
                        UserRepository userRepository,
                        VersionRepository versionRepository) {
        this.userService = userService;
        this.groupRepo = groupRepo;
        this.groupUserRepo = groupUserRepo;
        this.userRepository = userRepository;
        this.versionRepository = versionRepository;
    }


    //  Create
    @Transactional
    @JustAdmin
    public GetGroupHierarchyResDto createGroupAndReturnHierrachy(CreateGroupReqDto dto) {
        createGroup(dto);
        return new GetGroupHierarchyResDto(getGroupById(1L));
    }


    @Transactional
    @JustAdmin
    public GroupResDto createGroup(CreateGroupReqDto dto) {
        Group newGroup = createNewGroup(dto);
        Group savedNewGroup = groupRepo.save(newGroup);
        savedNewGroup.getDocuments().get(0).setTitle(newGroup.getGroupName() + " 그룹에 오신것을 환영합니다.");
        Version version = Version.builder()
                .content(newGroup.getGroupName() + " 그룹에 오신것을 환영합니다.")
                .document(savedNewGroup.getDocuments().get(0))
                .writer(findUserByEmployeeId(
                        SecurityContextHolder.getContext().getAuthentication().getName())).build();
        versionRepository.save(version);
        return new GroupResDto(savedNewGroup);
    }

    @Transactional
    @JustAdmin
    public List<GroupResDto> createGroups(List<CreateGroupReqDto> dtos) {
        return dtos.stream()
                .map(this::createGroup)
                .collect(Collectors.toList());
    }

    @JustAdmin
    public List<GroupUsersRoleResDto> addUsersToGroup(addUsersToGroupReqDto dto) {
        List<GroupUser> addedUsers = getGroupUsers(dto);
        return addedUsers.stream()
                .map(GroupUsersRoleResDto::new)
                .collect(Collectors.toList());
    }

    @JustAdmin
    public List<List<GroupUsersRoleResDto>> addUsersToGroupList(List<addUsersToGroupReqDto> dtos) {
        List<List<GroupUsersRoleResDto>> addResult = new ArrayList<>();
        for (addUsersToGroupReqDto dto : dtos)
            addResult.add(addUsersToGroup(dto));
        return addResult;
    }


    //  Read
    @UserAdmin
    public GroupResDto getGroupInfoById(Long groupId) {
        Group group = getGroupById(groupId);
        return new GroupResDto(group);
    }

    @UserAdmin
    public GetGroupHierarchyResDto getGroupHierarchy(Long groupId) {
        return new GetGroupHierarchyResDto(getGroupById(groupId));
    }

    @UserAdmin
    public GroupUsersResDto getGroupUsers(Long groupId) {
        Group group = getGroupById(groupId);
        return new GroupUsersResDto(group);
    }

    @UserAdmin
    public List<GroupResDto> getGroupsById(Long groupId) {
        return getGroupById(groupId).getChildGroups().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

    @UserAdmin
    public List<GroupResDto> getMyGroups() {
        User loginedUser = userService.getUserFromAuthentication();
        List<GroupUser> userGroups = groupUserRepo.findByUser(loginedUser);
        return userGroups.stream()
                .map(GroupUser::getGroup)
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }


    //  Update
    @JustAdmin
    @Transactional
    public GroupResDto updateGroupInfo(UpdateGroupReqDto dto) {

        if (Objects.equals(dto.getGroupId(), dto.getSuperGroupId()))
            throw new IllegalArgumentException("자기 자신에게 소속시킬 수 없습니다");

        Group group = getGroupById(dto.getGroupId());

        List<Long> childIdList = group.getAllChildGroupIds();
        if(childIdList.contains(dto.getSuperGroupId()))
            throw new IllegalArgumentException("자신의 하위 그룹으로 이동시킬 수 없습니다.");

        // 상위그룹에 null 로 들어오면 1번(root) 그룹 아래 설정
        if (dto.getSuperGroupId() == null)
            dto.setSuperGroupId(1L);

        group.setGroupName(dto.getGroupName());
        group.setGroupType(dto.getGroupType());

        // 상위그룹을 null 로 처리할 때 실행되는 함수 .
        Optional.ofNullable(dto.getSuperGroupId())
                .map(this::getGroupById)
                .ifPresent(group::setSuperGroup);

        return new GroupResDto(groupRepo.save(group));
    }

    @JustAdmin
    public GroupUsersRoleResDto updateUserRoleInGroup(Long groupId, Long userId) {
        GroupUser groupUser = getGroupUserByGroupUserId(groupId, userId);
        if (groupUser.getGroupRole().equals(GroupRole.SUPER))
            groupUser.setGroupRole(GroupRole.NORMAL);
        else
            groupUser.setGroupRole(GroupRole.SUPER);
        return new GroupUsersRoleResDto(groupUserRepo.save(groupUser));
    }

    //  Delete
    @JustAdmin
    public GetGroupHierarchyResDto deleteGroup(Long groupId) {
        Group targetGroup = getGroupById(groupId);
        if (!targetGroup.getChildGroups().isEmpty())
            throw new IllegalStateException("그룹에 하위 그룹이 존재하여 삭제할 수 없습니다.");
        if (targetGroup.getDocuments().size() > 1)
            throw new IllegalStateException("그룹에 최상단문서 1개만 남기고 모두 지워야 삭제 가능합니다.");
        groupRepo.delete(targetGroup);

        return new GetGroupHierarchyResDto(getGroupById(1L));
    }


    @JustAdmin
    public GroupUsersResDto removeUserFromGroup(Long groupId, Long userId) {
        GroupUser groupUser = getGroupUserByGroupUserId(groupId, userId);
        groupUserRepo.delete(groupUser);
        return getGroupUsers(groupId);
    }

    //  공통함수
    @UserAdmin
    public Group getGroupById(Long superGroupId) {
        return groupRepo.findById(superGroupId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "그룹 아이디로 검색할 수 있는 그룹이 없습니다. " + superGroupId));
    }

    @JustAdmin
    public Group createNewGroup(CreateGroupReqDto dto) {
        Group newGroup = dto.makeAuthorityReqDtoToGroup();
        Optional.ofNullable(dto.getSuperGroupId())
                .map(this::getGroupById)
                .ifPresent(newGroup::setSuperGroup);
        return newGroup;
    }

    @UserAdmin
    public List<GroupUser> getGroupUsers(addUsersToGroupReqDto dto) {
        System.out.println(dto.getGroupId() + "그룹 아이디");
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

    @UserAdmin
    public List<GroupUser> getGroupUserByGroup(Group group) {
        return groupUserRepo.findByGroup(group);
    }

    @UserAdmin
    public List<User> getAccessibleUsers(Long groupId) {
        Group myGroup = getGroupById(groupId);
        return getGroupUserByGroup(myGroup).stream()
                .map(GroupUser::getUser)
                .collect(Collectors.toList());
    }

    @UserAdmin
    public List<GroupResDto> getGroups() {
        return groupRepo.findAll().stream()
                .map(GroupResDto::new)
                .collect(Collectors.toList());
    }

    @JustAdmin
    public GroupUser getGroupUserByGroupUserId(Long groupId, Long userId) {
        GroupUserId groupUserId = GroupUserId.builder()
                .group(getGroupById(groupId))
                .user(userService.getUserById(userId))
                .build();
        return groupUserRepo.findById(groupUserId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "그룹 유저 아이디로 검색할 수 있는 그룹 유저가 없습니다. groupId: " + groupId + ", userId: " + userId));
    }

    private User findUserByEmployeeId(String employeeId) {
        return userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사번의 회원이 존재하지 않습니다."));
    }
}
