package com.FINAL.KIP.document.service;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import com.FINAL.KIP.group.service.GroupService;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public DocumentResDto createDocument(CreateDocumentReqDto dto) {
        Document newDocument = dto.makeDocDtoToDocument();
        if(dto.getUpLinkId() != null) {
            Document upLinkDoc = getDocumentById(dto.getUpLinkId());
            newDocument.setUpLink(upLinkDoc);
        }
        if(dto.getDownLinkId() != null) {
            Document downLinkDoc = getDocumentById(dto.getDownLinkId());
            newDocument.setDownLink(downLinkDoc);
        }
        Group group = groupService.getGroupById(
                dto.getGroupId()
        );
        newDocument.setGroup(group);
        return new DocumentResDto(documentRepo.save(newDocument));
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

//    중복함수
    public Document getDocumentById (Long documentId){
        return documentRepo.findById(documentId).orElse(null);
    }
}
