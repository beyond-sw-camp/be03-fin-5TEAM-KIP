package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.user.dto.res.UserAndGroupRoleResDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class GroupUsersResDto {

    private final String groupName;
    private final List<UserAndGroupRoleResDto> userList;


    public GroupUsersResDto(List<GroupUser> groupUsers) {

        // 그룹에 유저가 없을경우 널처리.
        Optional<Group> firstGroupOptional = groupUsers.stream()
                .map(GroupUser::getGroup)
                .findFirst();

        this.groupName = firstGroupOptional.map(Group::getGroupName).orElse("소속된 사원이 없습니다.");
        this.userList = !groupUsers.isEmpty()
                ? groupUsers.stream()
                .map(UserAndGroupRoleResDto::new)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }

}
