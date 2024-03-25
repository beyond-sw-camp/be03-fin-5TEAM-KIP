package com.FINAL.KIP.group.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "\"group\"") // group이 mariaDb예약어라 설정해줌.
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private GroupType groupType;

    @Setter
    @ManyToOne
    @JoinColumn
    private Group superGroup;

    @Setter
    private String delYn = "N";
    public Group() {}

    @OneToMany(mappedBy = "superGroup", cascade = CascadeType.PERSIST)
    private final List<Group> childGroups = new ArrayList<>();
    public void setGroups(List<Group> groups) {
        this.childGroups.clear();
        if (groups != null) this.childGroups.addAll(groups);
    }

    @OneToMany(mappedBy = "group", cascade = CascadeType.PERSIST)
    private final List<Document> documents = new ArrayList<>();

    @Builder
    public Group(String groupName, GroupType groupType) {
        this.groupName = groupName;
        this.groupType = groupType;
    }

    @PrePersist
    public void prePersist() {
        documents.add(
                Document.builder()
                        .title("empty")
                        .kmsDocType(KmsDocType.SECTION)
                        .group(this)
                        .build()
        );
    }

}
