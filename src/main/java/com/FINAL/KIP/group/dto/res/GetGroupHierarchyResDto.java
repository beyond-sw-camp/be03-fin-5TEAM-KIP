package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetGroupHierarchyResDto {
    private final Long groupId;
    private final String groupName;
    private final String groupType;
    private final List<GetGroupHierarchyResDto> childGroups;

    public GetGroupHierarchyResDto(Group group){
        this.groupId = group.getId();
        this.groupName = group.getGroupName();
        this.groupType = group.getGroupType().name();
        this.childGroups = group.getChildGroups().stream()
                .map(GetGroupHierarchyResDto::new)
                .collect(Collectors.toList());
    }
}
