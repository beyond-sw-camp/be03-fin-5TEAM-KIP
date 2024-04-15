package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupType;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.user.dto.res.UserAndGroupRoleResDto;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupUsersResDto {

    private final String groupName;
    private final GroupType groupType;
    private final List<UserAndGroupRoleResDto> userList;

    public GroupUsersResDto(Group group) {
        this.groupName = group.getGroupName();
        this.groupType = group.getGroupType();
        List<GroupUser> groupUsers = group.getGroupUsers();
        this.userList = !groupUsers.isEmpty()
                ? groupUsers.stream()
                .map(UserAndGroupRoleResDto::new)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
