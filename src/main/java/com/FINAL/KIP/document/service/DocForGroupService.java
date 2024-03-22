package com.FINAL.KIP.document.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocForGroupService {

    private final DocumentRepository documentRepo;

    @Autowired
    public DocForGroupService(DocumentRepository documentRepo) {
        this.documentRepo = documentRepo;
    }

//    Create
    public void createEmptyDocWhenGroupCreated(Group group, KmsDocType docType) {
        documentRepo.save(
                Document.builder()
                        .title("환영합니다.")
                        .kmsDocType(docType)
                        .group(group)
                        .build()
        );
    }

}
