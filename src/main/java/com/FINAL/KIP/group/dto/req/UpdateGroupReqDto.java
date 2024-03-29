package com.FINAL.KIP.group.dto.req;

import com.FINAL.KIP.group.domain.GroupType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupReqDto {
    private Long groupId;
    private String groupName;
    private GroupType groupType;
    private Long supperGroupId;
}
