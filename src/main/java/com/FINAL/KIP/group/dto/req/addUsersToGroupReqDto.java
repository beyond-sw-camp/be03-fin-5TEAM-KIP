package com.FINAL.KIP.group.dto.req;

import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class addUsersToGroupReqDto {

    private Long groupId;
    private List<UserIdAndGroupRole> groupUsers;


}
