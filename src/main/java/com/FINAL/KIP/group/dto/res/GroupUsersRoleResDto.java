package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.domain.GroupRole;
import lombok.Getter;

@Getter
public class GroupUsersRoleResDto {

    private final String groupName;
    private final Long groupId;
    private final String userName;
    private final Long userId;
    private final GroupRole groupRole;

    public GroupUsersRoleResDto(GroupUser group) {
        this.groupName = group.getGroup().getGroupName();
        this.groupId = group.getGroup().getId();
        this.userName = group.getUser().getName();
        this.userId = group.getUser().getId();
        this.groupRole = group.getGroupRole();
    }

}
