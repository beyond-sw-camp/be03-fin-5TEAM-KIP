package com.FINAL.KIP.group.dto.res;


import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.user.dto.res.UserResDto;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GroupResDto {

    private final Long groupId;
    private final String groupName;
    private final String groupType;
    // 상위그룹 리스트를 이름은 중복될 수 있으므로 아이디를 키값으로 하는 맵으로 저장.
    private final Map<Long, String> nameOfSuperGroups;
    private final List<UserResDto> usersInGroup;


    public GroupResDto(Group group){

        // 순서 보장 하는 LinkedHashMap 맵
        Map<Long, String>  nameOfSuperGroups = new LinkedHashMap<>();
        Group tempGroup = group.getSuperGroup();

        while (tempGroup != null) {  // 아이디와, 그룹이름을 보냄.
            nameOfSuperGroups.put(tempGroup.getId(), tempGroup.getGroupName());
            tempGroup = tempGroup.getSuperGroup();
        }
        // 그룹에 소속되어 있는 유저 정보 반환. (2중 널 처리)
        List<UserResDto> usersInGroup = group.getGroupUsers().stream()
                .map(GroupUser::getUser)
                .map(user -> Optional.ofNullable(user).map(UserResDto::new).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        this.groupId = group.getId();
        this.groupName = group.getGroupName();
        this.groupType = group.getGroupType().name();
        this.nameOfSuperGroups = nameOfSuperGroups;  // 프론트에서 순서를 뒤집이서 출력해줘야 함.
        this.usersInGroup = usersInGroup;
    }

}
