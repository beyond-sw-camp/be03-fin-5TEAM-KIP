package com.FINAL.KIP.document.service;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.req.moveDocInGroupReqDto;
import com.FINAL.KIP.document.dto.req.updateDocGroupIdReqDto;
import com.FINAL.KIP.document.dto.req.updateDocTitleReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.DocumentVersionResDto;
import com.FINAL.KIP.document.dto.res.GetDocumentResDto;
import com.FINAL.KIP.document.dto.res.PublicDocResDto;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.UserIdAndGroupRole;
import com.FINAL.KIP.group.service.GroupService;
import com.FINAL.KIP.hashtag.domain.DocHashTag;
import com.FINAL.KIP.hashtag.service.HashTagService;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.dto.request.VersionCreateReqDto;
import com.FINAL.KIP.version.dto.response.VersionDetailResDto;
import com.FINAL.KIP.version.dto.response.VersionReplaceResDto;
import com.FINAL.KIP.version.repository.VersionRepository;
import com.FINAL.KIP.version.service.VersionService;
import com.google.api.Http;
import java.util.UUID;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final HashTagService hashTagService;
    private final VersionService versionService;
    private final VersionRepository versionRepository;


    @Autowired
    public DocumentService(DocumentRepository documentRepo,
                           GroupService groupService,
                           UserService userService,
                           HashTagService hashTagService, VersionService versionService,
        VersionRepository versionRepository) {
        this.documentRepo = documentRepo;
        this.groupService = groupService;
        this.userService = userService;
        this.hashTagService = hashTagService;
		this.versionService = versionService;
        this.versionRepository = versionRepository;
    }

    //    Create
    @Transactional
    public DocumentResDto createDocument(CreateDocumentReqDto dto) {
        Document newDocument = dto.makeDocDtoToDocument();

        if (dto.getGroupId() != null) { // 전체공개문서와, 그룹 소속문서 연결.
            Group group = groupService.getGroupById(dto.getGroupId());
            newDocument.setGroup(group);

            Document upDocument = getDocumentById(dto.getUpLinkId());
            newDocument.setUpLink(upDocument);

            Document downDocument = upDocument.getDownLink();
            newDocument.setDownLink(downDocument);

            upDocument.setDownLink(newDocument);
            if (downDocument != null)
                downDocument.setUpLink(newDocument);
        }

        Document savedDocument = documentRepo.save(newDocument);
        versionService.createNewVersion(new VersionCreateReqDto(dto.getContent(), savedDocument));

        if (dto.getHashTags() != null && !dto.getHashTags().isEmpty()) {
            hashTagService.createHashTags(dto.getHashTags());  // 중복빼고 저장.
            List<DocHashTag> docHashTags = dto.getHashTags().stream()
                    .map(req -> hashTagService.getHashTagByTagName(req.getTagName()))
                    .map(hashTag -> new DocHashTag(savedDocument, hashTag))
                    .toList(); // 태그 이름으로 다시 아이디 추출하여 Doc과 연결
            savedDocument.addAllDocHashTags(docHashTags);
        }
        return new DocumentResDto(
                documentRepo.save(savedDocument));
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
    public DocumentResDto updatePublicDocumentGroupId(updateDocGroupIdReqDto dto) {

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

    @Transactional(readOnly = true)
	public ResponseEntity<List<DocumentVersionResDto>> getAllVersion(String uuid) {
        Document byUuid = findByUuid(uuid);

        List<Version> versions = byUuid.getVersions();
        List<DocumentVersionResDto> list = new ArrayList<>();
        for (Version version : versions) {
            list.add(new DocumentVersionResDto(
                version.getWriter().getName(),
                version.getCreated_at(),
                version.getIsShow()
            ));
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public Document findByUuid(String uuid) {
        UUID documentUuid = UUID.fromString(uuid);
        return documentRepo.findByUuid(documentUuid).orElseThrow(
            () -> new IllegalArgumentException("해당 문서가 존재하지 않습니다.")
        );
    }

    @Transactional
    public ResponseEntity<VersionReplaceResDto> replaceVersion(String documentUuid,
        Long versionId) {
        Document byUuid = findByUuid(documentUuid);
        Version showVersion = findShowVersion(byUuid);
        Version updateVersion = findVersionById(versionId);
        if (!showVersion.getDocument().equals(updateVersion.getDocument())) {
            throw new IllegalArgumentException("두개의 버젼의 글이 동일하지 않습니다.");
        }

        showVersion.updateIsShow();
        updateVersion.updateIsShow();

        return new ResponseEntity<>(new VersionReplaceResDto(showVersion.getId(),
            updateVersion.getId()), HttpStatus.OK);
    }

    public Version findShowVersion(Document document) {
        return versionRepository.findByDocumentAndIsShow(document, "Y").orElseThrow(
            () -> new IllegalArgumentException("알 수 없는 에러가 발생하였습니다.")
        );
    }

    public Version findVersionById(Long id) {
        return versionRepository.findById(id).orElseThrow(
            () -> new IllegalArgumentException("해당 버전이 존재하지 않습니다.")
        );
    }

    @Transactional(readOnly = true)
    public ResponseEntity<VersionDetailResDto> detailVersion(String documentUuid, Long versionId) {
        Document byUuid = findByUuid(documentUuid);
        Version version = findVersionById(versionId);

        return new ResponseEntity<>(new VersionDetailResDto(byUuid.getTitle(), version.getContent(),
            version.getWriter().getName(), version.getCreated_at()), HttpStatus.OK);
    }

}
