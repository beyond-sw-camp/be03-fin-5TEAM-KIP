package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class GetGroupHierarchyResDto {
    private final Long id;
    private final String title;
    private final List<GetGroupHierarchyResDto> children;

    public GetGroupHierarchyResDto(Group group){
        this.id = group.getId();
        this.title = group.getGroupName();
        this.children = group.getChildGroups().stream()
                .map(GetGroupHierarchyResDto::new)
                .collect(Collectors.toList());
    }
}
