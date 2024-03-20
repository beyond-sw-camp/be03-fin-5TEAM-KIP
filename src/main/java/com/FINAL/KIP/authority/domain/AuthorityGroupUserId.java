package com.FINAL.KIP.authority.domain;

import com.FINAL.KIP.user.domain.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class AuthorityGroupUserId implements Serializable {

    private AuthorityGroup authorityGroup;
    private User user;

}
