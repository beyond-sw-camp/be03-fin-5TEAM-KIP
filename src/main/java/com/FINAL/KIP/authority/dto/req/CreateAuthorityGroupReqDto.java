package com.FINAL.KIP.authority.dto.req;

import com.FINAL.KIP.authority.domain.AuthorityGroup;
import com.FINAL.KIP.authority.domain.GroupType;
import lombok.Getter;
import lombok.Setter;

@Setter
public class CreateAuthorityGroupReqDto {

    private String groupName;
    private String groupType;

    @Getter
    private Long supperGroupId;

    public AuthorityGroup makeAuthorityReqDtoToAuthorityGroup(CreateAuthorityGroupReqDto this){
        return AuthorityGroup.builder()
                .groupName(this.groupName)
                .groupType(GroupType.valueOf(this.groupType))
                .build();
    }

}
