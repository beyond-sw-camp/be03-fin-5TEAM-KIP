package com.FINAL.KIP.user.domain;


import com.FINAL.KIP.document.domain.Document;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@IdClass(UserDocAuthorityId.class)
public class UserDocAuthority {

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false)
    @Id
    private Document document;

    @Column(nullable = false)
    private LocalDateTime dueDate;

}
