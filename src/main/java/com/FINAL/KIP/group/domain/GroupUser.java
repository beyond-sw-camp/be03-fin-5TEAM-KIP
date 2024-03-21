package com.FINAL.KIP.group.domain;


import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@IdClass(GroupUserId.class)
public class GroupUser extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private Group group;

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private User user;


    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @Setter
    private String delYn = "N";

    public GroupUser(){}

    @Builder
    public GroupUser(Group group,
                     User user, String groupRole) {
        this.group = group;
        this.user = user;
        this.groupRole = groupRole == null ? null : GroupRole.valueOf(groupRole);
    }
}
