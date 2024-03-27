package com.FINAL.KIP.user.dto.res;

import com.FINAL.KIP.group.domain.GroupRole;
import com.FINAL.KIP.group.domain.GroupUser;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class UserAndGroupRoleResDto {

    private final Long userId;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final String profileImageUrl;
    private final LocalDateTime employedDay;
    private final String employeeId;
    private final GroupRole groupRole;

    public UserAndGroupRoleResDto(GroupUser groupUser){
        this.userId = groupUser.getUser().getId();
        this.name = groupUser.getUser().getName();
        this.email = groupUser.getUser().getEmail();
        this.phoneNumber = groupUser.getUser().getPhoneNumber();
        this.profileImageUrl = groupUser.getUser().getProfileImageUrl();
        this.employedDay = groupUser.getUser().getEmployedDay();
        this.employeeId = groupUser.getUser().getEmployeeId();
        this.groupRole = groupUser.getGroupRole();
    }
}
