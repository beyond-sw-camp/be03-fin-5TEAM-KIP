package com.FINAL.KIP.group.domain;

import com.FINAL.KIP.user.domain.User;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class GroupUserId implements Serializable {

    private Group group;
    private User user;

    public GroupUserId() {}

    @Builder
    public GroupUserId (Group group, User user) {
        this.group = group;
        this.user = user;
    }
}
