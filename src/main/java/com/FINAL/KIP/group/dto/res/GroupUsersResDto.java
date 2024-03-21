package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.user.dto.res.UserAndGroupRoleResDto;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GroupUsersResDto {

    private final String groupName;
    private final Long groupId;
    private final List<UserAndGroupRoleResDto> userList;


    public GroupUsersResDto(List<GroupUser> groupUsers) {
        this.groupName = groupUsers.get(0).getGroup().getGroupName();
        this.groupId = groupUsers.get(0).getGroup().getId();
        this.userList = groupUsers.stream()
                .map(UserAndGroupRoleResDto::new)
                .collect(Collectors.toList());
    }

}
