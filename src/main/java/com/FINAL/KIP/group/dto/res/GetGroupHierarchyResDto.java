package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupType;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetGroupHierarchyResDto {
    private final Long id;
    private final String title;
    private final GroupType groupType;
    private final Long superGroupId;
    private final String superGroupName;
    private final List<GetGroupHierarchyResDto> children;

    public GetGroupHierarchyResDto(Group group) {
        this.id = group.getId();
        this.title = group.getGroupName();
        this.groupType = group.getGroupType();
        Group superGroup = group.getSuperGroup();

        if (superGroup != null) {
            this.superGroupId = superGroup.getId();
            this.superGroupName = superGroup.getGroupName();
        }
        else {
            this.superGroupId = null;
            this.superGroupName = null;
        }

        this.children = group.getChildGroups().stream()
                .map(GetGroupHierarchyResDto::new)
                .collect(Collectors.toList());
    }
}
