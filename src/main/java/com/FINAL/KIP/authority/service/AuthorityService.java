package com.FINAL.KIP.authority.service;

import com.FINAL.KIP.authority.domain.AuthorityGroup;
import com.FINAL.KIP.authority.domain.AuthorityGroupUser;
import com.FINAL.KIP.authority.domain.UserIdAndGroupRole;
import com.FINAL.KIP.authority.dto.req.CreateAuthorityGroupReqDto;
import com.FINAL.KIP.authority.dto.req.addUsersToGroupReqDto;
import com.FINAL.KIP.authority.dto.res.AuthorityGroupResDto;
import com.FINAL.KIP.authority.dto.res.GetAuthorityGroupHierarchyResDto;
import com.FINAL.KIP.authority.dto.res.GroupUsersRoleResDto;
import com.FINAL.KIP.authority.repository.AuthorityGroupRepository;
import com.FINAL.KIP.authority.repository.AuthorityGroupUserRepository;
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
public class AuthorityService {

    private final UserService userService;
    private final AuthorityGroupRepository authorityGroupRepo;
    private final AuthorityGroupUserRepository authorityGroupUserRepo;

    @Autowired
    public AuthorityService(AuthorityGroupRepository authorityGroupRepo, UserService userService, AuthorityGroupUserRepository authorityGroupUserRepo) {
        this.userService = userService;
        this.authorityGroupRepo = authorityGroupRepo;
        this.authorityGroupUserRepo = authorityGroupUserRepo;
    }


    //    Create
    public AuthorityGroupResDto createAuthorityGroup(CreateAuthorityGroupReqDto dto) {
        AuthorityGroup newAuthorityGroup = createNewAuthorityGroup(dto);
        return new AuthorityGroupResDto(authorityGroupRepo.save(newAuthorityGroup));
    }

    public List<AuthorityGroupResDto> createAuthorityGroups(List<CreateAuthorityGroupReqDto> dtos) {
        return dtos.stream()
                .map(this::createAuthorityGroup)
                .collect(Collectors.toList());
    }

    public List<GroupUsersRoleResDto> addUsersToGroup(addUsersToGroupReqDto dto) {
        List<AuthorityGroupUser> addedUsers = getAuthorityGroupUsers(dto);
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
    public GetAuthorityGroupHierarchyResDto getAuthorityGroupHierarchy(Long authorityGroupId) {
        return new GetAuthorityGroupHierarchyResDto(getAuthorityGroupById(authorityGroupId));
    }

    public List<GroupUsersRoleResDto> getAuthorityGroupUsers(Long authorityGroupId) {
        AuthorityGroup authorityGroup = getAuthorityGroupById(authorityGroupId);
        return getByAuthorityGroup(authorityGroup).stream()
                .map(GroupUsersRoleResDto::new)
                .collect(Collectors.toList());
    }

    public List<AuthorityGroupResDto> getAuthorityGroupsById(Long authorityGroupId) {
        return getAuthorityGroupById(authorityGroupId).getChildGroups().stream()
                .map(AuthorityGroupResDto::new)
                .collect(Collectors.toList());
    }

    //    함수 공통화
    public AuthorityGroup getAuthorityGroupById(Long supperGroupId) {
        return authorityGroupRepo.findById(supperGroupId).orElseThrow(EntityNotFoundException::new);
    }


    public AuthorityGroup createNewAuthorityGroup(CreateAuthorityGroupReqDto dto) {
        AuthorityGroup newAuthorityGroup = dto.makeAuthorityReqDtoToAuthorityGroup();

        Optional.ofNullable(dto.getSupperGroupId())
                .map(this::getAuthorityGroupById)
                .ifPresent(newAuthorityGroup::setSuperGroup);

        return newAuthorityGroup;
    }

    public List<AuthorityGroupUser> getAuthorityGroupUsers(addUsersToGroupReqDto dto) {
        List<AuthorityGroupUser> addedUsers = new ArrayList<>();
        AuthorityGroup authorityGroup = getAuthorityGroupById(dto.getGroupId());
        for (UserIdAndGroupRole user : dto.getGroupUsers()) {
            User tempUser = userService.getUserById(user.getUserId());
            AuthorityGroupUser newAuthorityGroupUser = AuthorityGroupUser.builder()
                    .authorityGroup(authorityGroup)
                    .user(tempUser)
                    .groupRole(user.getGroupRole())
                    .build();
            addedUsers.add(authorityGroupUserRepo.save(newAuthorityGroupUser));
        }
        return addedUsers;
    }

    public List<AuthorityGroupUser> getByAuthorityGroup(AuthorityGroup authorityGroup) {
        return authorityGroupUserRepo.findByAuthorityGroup(authorityGroup);
    }


    public List<UserIdAndGroupRole> getAccessibleUsers(Long authorityGroupId) {
        List<UserIdAndGroupRole> accessibleUsers = new ArrayList<>();
        List<Long> authorityGroupIdList = new ArrayList<>();

        AuthorityGroup authorityGroup = getAuthorityGroupById(authorityGroupId);

        do {
            authorityGroupIdList.add(authorityGroup.getId());
            authorityGroup = authorityGroup.getSuperGroup();
        } while (authorityGroup.getSuperGroup() != null);
        authorityGroupIdList.add(authorityGroup.getId());

        for(Long superGroupId : authorityGroupIdList)
            System.out.println("상위 그룹 아이디" + superGroupId);

        for(Long superGroupId : authorityGroupIdList) {
            AuthorityGroup superGroup = getAuthorityGroupById(superGroupId);
            List<AuthorityGroupUser> superGroupUsers = getByAuthorityGroup(superGroup);
            for (AuthorityGroupUser authorityGroupUser : superGroupUsers) {
                UserIdAndGroupRole userIdAndGroupRole = new UserIdAndGroupRole();
                userIdAndGroupRole.setUserId(authorityGroupUser.getUser().getId());
                userIdAndGroupRole.setGroupRole(authorityGroupUser.getGroupRole().name());
                accessibleUsers.add(userIdAndGroupRole);
            }
        }
        return accessibleUsers;
    }

    public List<AuthorityGroupResDto> getAuthorityGroups() {
        return authorityGroupRepo.findAll().stream()
                .map(AuthorityGroupResDto::new)
                .collect(Collectors.toList());
    }

}
