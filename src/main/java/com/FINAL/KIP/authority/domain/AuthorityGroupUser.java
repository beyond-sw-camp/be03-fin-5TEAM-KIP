package com.FINAL.KIP.authority.domain;


import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@IdClass(AuthorityGroupUserId.class)
public class AuthorityGroupUser extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private AuthorityGroup authorityGroup;

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id private User user;


    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GroupRole groupRole;

    @Setter
    private String delYn = "N";

    public AuthorityGroupUser(){}

    @Builder
    public AuthorityGroupUser(AuthorityGroup authorityGroup,
                              User user, String groupRole) {
        this.authorityGroup = authorityGroup;
        this.user = user;
        this.groupRole = groupRole == null ? null : GroupRole.valueOf(groupRole);
    }
}
