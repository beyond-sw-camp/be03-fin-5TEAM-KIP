package com.FINAL.KIP.authority.dto.req;

import com.FINAL.KIP.authority.domain.UserIdAndGroupRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class addUsersToGroupReqDto {

    private Long groupId;
    private List<UserIdAndGroupRole> groupUsers;


}
