package com.FINAL.KIP.authority.dto.res;


import com.FINAL.KIP.authority.domain.AuthorityGroup;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetAuthorityGroupHierarchyResDto {
    private final Long groupId;
    private final String groupName;
    private final String groupType;
    private final List<GetAuthorityGroupHierarchyResDto> childGroups;

    public GetAuthorityGroupHierarchyResDto(AuthorityGroup authorityGroup){
        this.groupId = authorityGroup.getId();
        this.groupName = authorityGroup.getGroupName();
        this.groupType = authorityGroup.getGroupType().name();
        this.childGroups = authorityGroup.getChildGroups().stream()
                .map(GetAuthorityGroupHierarchyResDto::new)
                .collect(Collectors.toList());
    }
}
