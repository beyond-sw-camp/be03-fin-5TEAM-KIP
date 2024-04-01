package com.FINAL.KIP.document.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
public class DocBookReport {

    protected DocBookReport(){
    }

    public DocBookReport(Document document, String employeeId){
        this.document = document;
        this.employeeId = employeeId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id")
    private Document document;

    @JoinColumn(name = "employeeId")
    private String employeeId;

    @CreationTimestamp
    private LocalDateTime bookCreatedTime;
}
