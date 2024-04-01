package com.FINAL.KIP.hashtag.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String tagName;

}
