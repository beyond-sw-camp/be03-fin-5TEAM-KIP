package com.FINAL.KIP.document.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.req.moveDocInGroupReqDto;
import com.FINAL.KIP.document.dto.req.updateDocGroupIdReqDto;
import com.FINAL.KIP.document.dto.req.updateDocTitleReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.dto.res.PublicDocResDto;
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
import java.util.Objects;
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
        if (dto.getGroupId() == null) {
            return new DocumentResDto(
                    documentRepo.save(
                            dto.makeDocDtoToDocument()));
        } else {
            Document upDocument = getDocumentById(dto.getUpLinkId());

            Document newDocument = dto.makeDocDtoToDocument();
            newDocument.setUpLink(upDocument);

            Document downDocument = upDocument.getDownLink();
            newDocument.setDownLink(downDocument);

            Group group = groupService.getGroupById(dto.getGroupId());
            newDocument.setGroup(group);

            Document savedDocument = documentRepo.save(newDocument);
            upDocument.setDownLink(savedDocument);

            if (downDocument != null)
                downDocument.setUpLink(savedDocument);
            return new DocumentResDto(savedDocument);
        }
    }


    //    Read
    public List<PublicDocResDto> getPublicDocuments() {
        return documentRepo.findAll().stream()
                .filter(document -> document.getGroup() == null)
                .map(PublicDocResDto::new)
                .collect(Collectors.toList());
    }

    public GetDocumentResDto GetIsAccessibleDoc(Long docId, Long userId) {
        boolean isAccessible = false;
        Document tryToOpenDocument = getDocumentById(docId);
        User tryUser = userService.getUserById(userId);

        Long GroupId = tryToOpenDocument.getGroup().getId();

        List<UserIdAndGroupRole> accessibleUsers = groupService.getAccessibleUsers(GroupId);
        for (UserIdAndGroupRole obj : accessibleUsers) {
            System.out.println(
                    "맴버 아이디: " + obj.getUserId() + " " +
                            userService.getUserById(obj.getUserId()).getName() +
                            " / 권한정보: " + obj.getGroupRole());
        }
        isAccessible = accessibleUsers.stream()
                .anyMatch(reqUserId -> reqUserId.getUserId().equals(tryUser.getId()));

        return new GetDocumentResDto(tryToOpenDocument, isAccessible);
    }

    public List<GetDocumentResDto> getLinkedDocumentsByGroupId(Long groupId) {
        List<Document> linkedDocuments = new ArrayList<>();
        Group targetGroup = groupService.getGroupById(groupId);
        Document topDocument = getTopDocument(targetGroup);

        linkedDocuments.add(topDocument);
        Document currentDocument = topDocument;
        while (currentDocument.getDownLink() != null) {
            currentDocument = currentDocument.getDownLink();
            linkedDocuments.add(currentDocument);
        }

        return linkedDocuments.stream()
                .map(document -> new GetDocumentResDto(document, true))
                .collect(Collectors.toList());
    }


    //    Update
    public DocumentResDto updateDocumentTitle(updateDocTitleReqDto dto) {
        Document targetDocument = getDocumentById(dto.getTargetDocumentId());
        targetDocument.setTitle(dto.getNewTitle());
        return new DocumentResDto(documentRepo.save(targetDocument));
    }

    @Transactional
    public List<GetDocumentResDto> moveDocumentInGroup(moveDocInGroupReqDto dto) throws IllegalArgumentException {

        if (Objects.equals(dto.getStartDocId(), dto.getEndDocId()))
            throw new IllegalArgumentException("이동하려는 아이디가 서로 같습니다.");

        Document startDocument = getDocumentById(dto.getStartDocId());
        Document endDocument = getDocumentById(dto.getEndDocId());

        Document upLinkedDoc = startDocument.getUpLink();
        Document downLinkedDoc = startDocument.getDownLink();

        // step 1
        if (upLinkedDoc != null) // start 문서가 최상단인 경우
            upLinkedDoc.setDownLink(downLinkedDoc);
        if (downLinkedDoc != null) // start 문서가 최하단인 경우
            downLinkedDoc.setUpLink(upLinkedDoc);

        // step 2
        startDocument.setUpLink(endDocument);
        startDocument.setDownLink(endDocument.getDownLink());

        // step 3
        if (endDocument.getDownLink() != null) // end 문서가 최하단 문서인경우
            endDocument.getDownLink().setUpLink(startDocument);
        endDocument.setDownLink(startDocument); // end 문서 최상단은 Null 불가.

        // 정렬된 문서리스트 리턴
        return getLinkedDocumentsByGroupId(startDocument.getGroup().getId());
    }

    @Transactional
    public DocumentResDto updateDocumentType(Long documentId) {
        Document targetDocument = getDocumentById(documentId);
        KmsDocType targetDocType = targetDocument.getKmsDocType();

        if (targetDocType.equals(KmsDocType.CONTENT))
            targetDocument.setKmsDocType(KmsDocType.SECTION);
        else
            targetDocument.setKmsDocType(KmsDocType.CONTENT);
        return new DocumentResDto(targetDocument);
    }

    @Transactional
    public DocumentResDto updateDocumentPublic(Long documentId) throws IllegalArgumentException {
        Document targetDocumnet = getDocumentById(documentId);

        if (targetDocumnet.getUpLink() == null && targetDocumnet.getGroup() != null)
            throw new IllegalArgumentException("최상단 문서는 전체공개가 불가능합니다.");

        if (targetDocumnet.getUpLink() == null)
            throw new IllegalArgumentException("이미 전체공개 문서입니다.");

        Document upDocument = targetDocumnet.getUpLink();
        Document downDocument = targetDocumnet.getDownLink();

        if (downDocument != null) // Target의 위, 아래 문서 연결
            downDocument.setUpLink(upDocument);
        upDocument.setDownLink(downDocument);

        targetDocumnet.setGroup(null); // 전체공개
        targetDocumnet.setUpLink(null);
        targetDocumnet.setDownLink(null);

        return new DocumentResDto(targetDocumnet);
    }

    @Transactional
    public DocumentResDto updatePublicDocumentGroupId (updateDocGroupIdReqDto dto){

        Document targetDocument = getDocumentById(dto.getTargetDocumentId());
        if (targetDocument.getGroup() != null)
            throw new IllegalArgumentException("전체공개 문서가 아닙니다.");

        Group targetGroup = groupService.getGroupById(dto.getTargetGroupId());
        Document topDocInTargetGroup = getTopDocument(targetGroup);
        Document downDocumnet = topDocInTargetGroup.getDownLink();

        // step 1
        targetDocument.setUpLink(topDocInTargetGroup);
        targetDocument.setDownLink(downDocumnet);

        // step 2
        targetDocument.setGroup(targetGroup);
        topDocInTargetGroup.setDownLink(targetDocument);
        if (downDocumnet != null)
            downDocumnet.setUpLink(targetDocument);

        return new DocumentResDto(targetDocument);
    }

    //    Delete
    @Transactional
    public void deleteDocument(Long documentId) throws IllegalArgumentException {
        Document tagetDocument = getDocumentById(documentId);
        if (tagetDocument.getGroup() == null)
            documentRepo.delete(tagetDocument);
        else if (tagetDocument.getUpLink() == null)
            throw new IllegalArgumentException("그룹의 최상단 문서는 삭제할 수 없습니다.");
        else {
            documentRepo.delete(tagetDocument);
            Document upLinkedDoc = tagetDocument.getUpLink();
            Document downLinkedDoc = tagetDocument.getDownLink();
            upLinkedDoc.setDownLink(downLinkedDoc);
            if (downLinkedDoc != null)
                downLinkedDoc.setUpLink(upLinkedDoc);
        }
    }

    //    공통함수
    public Document getDocumentById(Long documentId) throws NoSuchElementException {
        return documentRepo.findById(documentId)
                .orElseThrow(() -> new NoSuchElementException("찾으시려는 문서 ID와 일치하는 문서가 없습니다."));
    }

    private Document getTopDocument(Group targetGroup) throws NoSuchElementException {
        return targetGroup.getDocuments().stream()
                .filter(document -> document.getUpLink() == null)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(" 최상단 문서가 없습니다."));
    }
}
