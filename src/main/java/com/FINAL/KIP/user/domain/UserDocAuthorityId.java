package com.FINAL.KIP.user.domain;

import com.FINAL.KIP.document.domain.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class UserDocAuthorityId implements Serializable {

    private User user;
    private Document document;

}
