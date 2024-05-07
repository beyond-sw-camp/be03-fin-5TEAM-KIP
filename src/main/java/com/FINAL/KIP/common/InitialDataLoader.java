package com.FINAL.KIP.common;

import com.FINAL.KIP.document.dto.req.CreateDocumentReqDto;
import com.FINAL.KIP.document.service.DocumentService;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupType;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.user.domain.Role;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import com.FINAL.KIP.version.domain.Version;
import com.FINAL.KIP.version.repository.VersionRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final VersionRepository versionRepository;
    private final DocumentService documentService;

public InitialDataLoader(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         GroupRepository groupRepository,
                         VersionRepository versionRepository,
                         DocumentService documentService){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.groupRepository = groupRepository;
    this.versionRepository = versionRepository;
    this.documentService = documentService;
}

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 어드민 계정이 없으면
        if (userRepository.findByEmployeeId("k-1234567890").isEmpty()) {

            // 어드민 생성
            User adminMember = User.builder()
                    .name("삼성정밀")
                    .email("admin@kip.com")
                    .password(passwordEncoder.encode("1234"))
                    .phoneNumber("01012345678")
                    .profileImageUrl("https://picsum.photos/400") // 임시 프로필
                    .employeeId("k-1234567890")
                    .employedDay("1977년 8월 1일 월요일")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminMember);

            // 기본 그룹 생성
            User adminUser = userRepository.findById(1L).orElse(null);
            Group newGroup = Group.builder()
                    .groupName("한화시스템")
                    .groupType(GroupType.DEPARTMENT)
                    .build();
            Group savedNewGroup = groupRepository.save(newGroup);
            savedNewGroup.getDocuments().get(0).setTitle(newGroup.getGroupName() + " 그룹에 오신것을 환영합니다.");
            Version version = Version.builder()
                    .content(newGroup.getGroupName() + " 그룹에 오신것을 환영합니다.")
                    .document(savedNewGroup.getDocuments().get(0))
                    .writer(adminUser).build();
            versionRepository.save(version);

            // 전체공개문서 하나 생성
            CreateDocumentReqDto publicDoc = new CreateDocumentReqDto();
            publicDoc.setTitle("공지사항");
            publicDoc.setContent("공지사항 입니다.");
            publicDoc.setGroupId(null);
            documentService.createInitialPublicDoc(publicDoc, adminUser);
        }
    }
}
