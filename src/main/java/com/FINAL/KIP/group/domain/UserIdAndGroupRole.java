package com.FINAL.KIP.group.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserIdAndGroupRole {
    private Long userId;
    private String userName;
    private GroupRole groupRole;

    public UserIdAndGroupRole (GroupUser groupUser) {
        this.userId = groupUser.getUser().getId();
        if (groupUser.getUser().getName() != null)
            this.userName = groupUser.getUser().getName();
        this.groupRole = groupUser.getGroupRole();
    }
}
