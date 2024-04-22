package com.FINAL.KIP.hashtag.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class HashTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(unique = true, nullable = false)
    private String tagName;

    @OneToMany(mappedBy = "hashTag", cascade = CascadeType.REMOVE)
    private final List<DocHashTag> docHashTags = new ArrayList<>();

    public HashTag() {
    }
    @Builder
    public HashTag(String tagName) {
        this.tagName = tagName;
    }

}
