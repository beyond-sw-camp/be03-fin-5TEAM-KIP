package com.FINAL.KIP.document.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import com.FINAL.KIP.group.service.GroupService;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepo;
    private final GroupService groupService;
    private final UserService userService;


    @Autowired
    public DocumentService(DocumentRepository documentRepo,
                           GroupService groupService,
                           UserService userService) {
        this.documentRepo = documentRepo;
        this.groupService = groupService;
        this.userService = userService;
    }

//    Create
    @Transactional
    public DocumentResDto createDocument(CreateDocumentReqDto dto) {
        Document upDocumnet = getDocumentById(dto.getUpLinkId());

        Document newDocument = dto.makeDocDtoToDocument();
        newDocument.setUpLink(upDocumnet);

        Document downDocumnet = upDocumnet.getDownLink();
        newDocument.setDownLink(downDocumnet);

        Group group = groupService.getGroupById(dto.getGroupId());
        newDocument.setGroup(group);

        Document savedDocumnet = documentRepo.save(newDocument);
        upDocumnet.setDownLink(savedDocumnet);

        if(downDocumnet != null)
            downDocumnet.setUpLink(savedDocumnet);
        return new DocumentResDto(savedDocumnet);

    }

//    Read
    public GetDocumentResDto GetIsAccessibleDoc(Long docId, Long userId){
        boolean isAccessible = false;
        Document tryToOpenDocument = getDocumentById(docId);
        User tryUser = userService.getUserById(userId);

        Long GroupId = tryToOpenDocument.getGroup().getId();

        List<UserIdAndGroupRole> accessibleUsers = groupService.getAccessibleUsers(GroupId);
        for(UserIdAndGroupRole obj : accessibleUsers) {
            System.out.println(
                    "맴버 아이디: " + obj.getUserId() + " " +
                    userService.getUserById(obj.getUserId()).getName() +
                    " / 권한정보: " + obj.getGroupRole());
        }
        isAccessible = accessibleUsers.stream()
                .anyMatch(reqUserId -> reqUserId.getUserId().equals(tryUser.getId()));

        return new GetDocumentResDto(tryToOpenDocument, isAccessible);
    }

    public List<GetDocumentResDto> getLinkedDocumentsByGroup(Long groupId) {
        List<Document> linkedDocuments = new ArrayList<>();
        Group targetGroup = groupService.getGroupById(groupId);
        Document topDocumnet = getTopDocumnet(targetGroup);

        linkedDocuments.add(topDocumnet);
        Document currentDocument = topDocumnet;
        while (currentDocument.getDownLink() != null) {
            currentDocument = currentDocument.getDownLink();
            linkedDocuments.add(currentDocument);
        }

        return linkedDocuments.stream()
                .map(document -> new GetDocumentResDto(document, true))
                .collect(Collectors.toList());
    }


//    중복함수

    public Document getDocumentById (Long documentId){
        return documentRepo.findById(documentId)
                .orElseThrow(()-> new NoSuchElementException("찾으시려는 문서 ID와 일치하는 문서가 없습니다."));
    }
    private Document getTopDocumnet(Group targetGroup) {
        return targetGroup.getDocuments().stream()
                .filter(document -> document.getUpLink() == null)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(" 최상단 문서가 없습니다."));
    }
}
