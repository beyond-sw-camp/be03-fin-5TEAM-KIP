package com.FINAL.KIP.group.domain;

import com.FINAL.KIP.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class GroupUserId implements Serializable {

    private Group group;
    private User user;

}
