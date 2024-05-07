package com.FINAL.KIP.document.service;

import com.FINAL.KIP.common.s3.S3Config;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.document.domain.KmsDocType;
import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.dto.req.moveDocInGroupReqDto;
import com.FINAL.KIP.document.dto.req.updateDocGroupIdReqDto;
import com.FINAL.KIP.document.dto.req.updateDocTitleReqDto;
import com.FINAL.KIP.document.dto.res.DocumentResDto;
import com.FINAL.KIP.document.dto.res.DocumentVersionResDto;
import com.FINAL.KIP.document.dto.res.JustDocTitleResDto;
import com.FINAL.KIP.document.repository.DocumentRepository;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.service.GroupService;
import com.FINAL.KIP.hashtag.service.HashTagService;
import com.FINAL.KIP.request.repository.RequestRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.service.UserService;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.dto.response.VersionReplaceResDto;
import com.FINAL.KIP.version.repository.VersionRepository;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentService {

	private final DocumentRepository documentRepo;
	private final GroupService groupService;
	private final UserService userService;
	private final HashTagService hashTagService;
	private final VersionRepository versionRepository;
	private final RequestRepository requestRepository;
	private final S3Config s3Config;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;


	@Autowired
	public DocumentService(DocumentRepository documentRepo,
		GroupService groupService,
		UserService userService,
		HashTagService hashTagService,
		VersionRepository versionRepository, RequestRepository requestRepository, S3Config s3Config) {
		this.documentRepo = documentRepo;
		this.groupService = groupService;
		this.userService = userService;
		this.hashTagService = hashTagService;
		this.versionRepository = versionRepository;
		this.requestRepository = requestRepository;
		this.s3Config = s3Config;
	}

	//    Create
	@Transactional
	public DocumentResDto createDocument(CreateDocumentReqDto dto) {
		Document newDocument = dto.makeDocDtoToDocument();

		if (dto.getGroupId() != null) { // 비공개 문서 생성시.
			Group group = groupService.getGroupById(dto.getGroupId());
			Document upDocument = getDocumentById(dto.getUpLinkId());

            if (group.getDocuments().stream()
                .filter(doc -> doc.getId().equals(upDocument.getId()))
                .findAny().isEmpty()) // 서로 다른 그룹끼리 문서 연결 요청이 들어 오는 것 방지
            {
                throw new IllegalArgumentException(
                    "만들려는 문서의 ID 가 해당 그룹에 속해있지 않습니다.");
            }

			newDocument.setGroup(group);
			newDocument.setUpLink(upDocument);

			Document downDocument = upDocument.getDownLink();
			newDocument.setDownLink(downDocument);

			upDocument.setDownLink(newDocument);
            if (downDocument != null) {
                downDocument.setUpLink(newDocument);
            }
		}
		Document savedDocument = documentRepo.save(newDocument);

		User tryUser = userService.getUserFromAuthentication();
		Version newVersion = Version.builder()
			.content(dto.getContent())
			.writer(tryUser)
			.document(savedDocument)
			.build();
		versionRepository.save(newVersion);
		// 해시서비스로 공통화 시킴
        if (dto.getHashTags() != null && !dto.getHashTags().isEmpty()) {
            hashTagService.generateDocHashTags(dto.getHashTags(), savedDocument);
        }
		return new DocumentResDto(documentRepo.save(savedDocument), true);
	}

	// 최초 공개문서 하나 만드는 함수
	@Transactional
	public void createInitialPublicDoc(CreateDocumentReqDto dto , User amdinUser) {
		Document newDocument = dto.makeDocDtoToDocument();
		Document savedDocument = documentRepo.save(newDocument);
		Version newVersion = Version.builder()
				.content(dto.getContent())
				.document(savedDocument)
				.writer(amdinUser)
				.build();
		versionRepository.save(newVersion);
		documentRepo.save(savedDocument);
	}

	//    Read
	public List<JustDocTitleResDto> getPublicDocuments() {
		return documentRepo.findByGroupIsNull().stream()
				.map(JustDocTitleResDto::new)
				.collect(Collectors.toList());
	}

	public DocumentResDto getIsAccessibleDoc(Long documentId) {
		boolean isAccessible = true;
		Document tryDocument = getDocumentById(documentId);
		User tryUser = userService.getUserFromAuthentication();
		if(tryDocument.getGroup() != null) {
			Long docGroupId = tryDocument.getGroup().getId();
			List<User> accessibleUsers = groupService.getAccessibleUsers(docGroupId);
			accessibleUsers.add(userService.getUserById(1L)); // 관리자 추가
			/* 추후 파일별 접근 가능한 맴버 추가 로직 삽입 */
			isAccessible = accessibleUsers.stream() // 접근 가능 유저 체크
					.anyMatch(user -> user.equals(tryUser));
			if(!isAccessible)
				isAccessible = requestRepository.availableDocument(tryUser, tryDocument);
		}
		return new DocumentResDto(tryDocument, isAccessible);
	}

	public List<JustDocTitleResDto> getLinkedDocumentsByGroupId(Long groupId) {
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
			.map(JustDocTitleResDto::new)
			.collect(Collectors.toList());
	}


	//    Update
	public JustDocTitleResDto updateDocumentTitle(updateDocTitleReqDto dto) {
		Document targetDocument = getDocumentById(dto.getTargetDocumentId());
		targetDocument.setTitle(dto.getNewTitle());
		return new JustDocTitleResDto(documentRepo.save(targetDocument));
	}

	@Transactional
	public List<JustDocTitleResDto> moveDocumentInGroup(moveDocInGroupReqDto dto)
		throws IllegalArgumentException {

        if (Objects.equals(dto.getStartDocId(), dto.getEndDocId())) {
            throw new IllegalArgumentException("이동하려는 아이디가 서로 같습니다.");
        }

		Document startDocument = getDocumentById(dto.getStartDocId());
		Document endDocument = getDocumentById(dto.getEndDocId());

		Document upLinkedDoc = startDocument.getUpLink();
		Document downLinkedDoc = startDocument.getDownLink();

		// step 1
        if (upLinkedDoc != null) // start 문서가 최상단인 경우
        {
            upLinkedDoc.setDownLink(downLinkedDoc);
        }
        if (downLinkedDoc != null) // start 문서가 최하단인 경우
        {
            downLinkedDoc.setUpLink(upLinkedDoc);
        }

		// step 2
		startDocument.setUpLink(endDocument);
		startDocument.setDownLink(endDocument.getDownLink());

		// step 3
        if (endDocument.getDownLink() != null) // end 문서가 최하단 문서인경우
        {
            endDocument.getDownLink().setUpLink(startDocument);
        }
		endDocument.setDownLink(startDocument); // end 문서 최상단은 Null 불가.

		// 정렬된 문서리스트 리턴
		return getLinkedDocumentsByGroupId(startDocument.getGroup().getId());
	}

	@Transactional
	public DocumentResDto updateDocumentType(Long documentId) {
		Document targetDocument = getDocumentById(documentId);
		KmsDocType targetDocType = targetDocument.getKmsDocType();

        if (targetDocType.equals(KmsDocType.CONTENT)) {
            targetDocument.setKmsDocType(KmsDocType.SECTION);
        } else {
            targetDocument.setKmsDocType(KmsDocType.CONTENT);
        }
		return new DocumentResDto(targetDocument, true);
	}

	@Transactional
	public DocumentResDto updateDocumentPublic(Long documentId) throws IllegalArgumentException {
		Document targetDocumnet = getDocumentById(documentId);

        if (targetDocumnet.getUpLink() == null && targetDocumnet.getGroup() != null) {
            throw new IllegalArgumentException("최상단 문서는 전체공개가 불가능합니다.");
        }

        if (targetDocumnet.getUpLink() == null) {
            throw new IllegalArgumentException("이미 전체공개 문서입니다.");
        }

		Document upDocument = targetDocumnet.getUpLink();
		Document downDocument = targetDocumnet.getDownLink();

        if (downDocument != null) // Target의 위, 아래 문서 연결
        {
            downDocument.setUpLink(upDocument);
        }
		upDocument.setDownLink(downDocument);

		targetDocumnet.setGroup(null); // 전체공개
		targetDocumnet.setUpLink(null);
		targetDocumnet.setDownLink(null);

		return new DocumentResDto(targetDocumnet, true);
	}

	@Transactional
	public DocumentResDto updatePublicDocumentGroupId(updateDocGroupIdReqDto dto) {

		Document targetDocument = getDocumentById(dto.getTargetDocumentId());
        if (targetDocument.getGroup() != null) {
            throw new IllegalArgumentException("전체공개 문서가 아닙니다.");
        }

		Group targetGroup = groupService.getGroupById(dto.getTargetGroupId());
		Document topDocInTargetGroup = getTopDocument(targetGroup);
		Document downDocumnet = topDocInTargetGroup.getDownLink();

		// step 1
		targetDocument.setUpLink(topDocInTargetGroup);
		targetDocument.setDownLink(downDocumnet);

		// step 2
		targetDocument.setGroup(targetGroup);
		topDocInTargetGroup.setDownLink(targetDocument);
        if (downDocumnet != null) {
            downDocumnet.setUpLink(targetDocument);
        }

		return new DocumentResDto(targetDocument, true);
	}

	//    Delete
	@Transactional
	public void deleteDocument(Long documentId) throws IllegalArgumentException {
		Document tagetDocument = getDocumentById(documentId);
        if (tagetDocument.getGroup() == null) {
            documentRepo.delete(tagetDocument);
        } else if (tagetDocument.getUpLink() == null) {
            throw new IllegalArgumentException("그룹의 최상단 문서는 삭제할 수 없습니다.");
        } else {
            documentRepo.delete(tagetDocument);
            Document upLinkedDoc = tagetDocument.getUpLink();
            Document downLinkedDoc = tagetDocument.getDownLink();
            upLinkedDoc.setDownLink(downLinkedDoc);
            if (downLinkedDoc != null) {
                downLinkedDoc.setUpLink(upLinkedDoc);
            }
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
	public ResponseEntity<List<DocumentVersionResDto>> getAllVersion(Long id) {
		Document document = findDocumentById(id);

		List<Version> versions = document.getVersions();
		versions.sort((v1, v2) -> v2.getCreated_at().compareTo(v1.getCreated_at()));
		List<DocumentVersionResDto> list = new ArrayList<>();
		for (Version version : versions) {
			list.add(new DocumentVersionResDto(
				version.getId(),
				version.getWriter().getName(),
				version.getCreated_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
				version.getMessage(),
				version.getIsShow()
			));
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	public Document findDocumentById(Long documentId) {
		return documentRepo.findById(documentId).orElseThrow(
			() -> new IllegalArgumentException("해당 문서가 존재하지 않습니다.")
		);
	}

	@Transactional
	public ResponseEntity<VersionReplaceResDto> replaceVersion( Long versionId ) {
		Version updateVersion = findVersionById(versionId);
		Document document = updateVersion.getDocument();
		Version showVersion = findShowVersion(document);

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

	public String imageSave(MultipartFile image) throws IOException {
		String orgFilename = image.getOriginalFilename();
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String extension = orgFilename.substring(orgFilename.lastIndexOf(".") + 1);
		String saveFilename = uuid + "." + extension;

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(image.getSize());
		metadata.setContentType(image.getContentType());

		s3Config.amazonS3Client().putObject(bucket, "documentImg/" + saveFilename, image.getInputStream(), metadata);
		return s3Config.amazonS3Client().getUrl(bucket, "documentImg/" + saveFilename).toString();
	}
}
