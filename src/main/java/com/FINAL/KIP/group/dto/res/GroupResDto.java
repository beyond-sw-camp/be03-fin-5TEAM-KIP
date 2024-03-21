package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import lombok.Getter;

@Getter
public class GroupResDto {

    private final Long groupId;
    private final String groupName;
    private final String groupType;
    private final Long superGroupId;

    public GroupResDto(Group group){
        this.groupId = group.getId();
        this.groupName = group.getGroupName();
        this.groupType = group.getGroupType().name();
        this.superGroupId = group.getSuperGroup() != null ? group.getSuperGroup().getId() : null;
    }

}
