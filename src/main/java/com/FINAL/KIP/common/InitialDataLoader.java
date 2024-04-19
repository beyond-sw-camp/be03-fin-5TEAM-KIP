package com.FINAL.KIP.common;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupType;
import com.FINAL.KIP.group.repository.GroupRepository;
import com.FINAL.KIP.user.domain.Role;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;

public InitialDataLoader(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         GroupRepository groupRepository){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.groupRepository = groupRepository;
}

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmployeeId("k-1234567890").isEmpty()) {
            User adminMember = User.builder()
                    .name("관리자")
                    .email("admin@kip.com")
                    .password(passwordEncoder.encode("1234"))
                    .phoneNumber("01012345678")
                    .profileImageUrl("https://picsum.photos/400") // 임시 프로필
                    .employeeId("k-1234567890")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminMember);
        }
        if (groupRepository.findById(1L).isEmpty()) {
            Group adminGroup = Group.builder()
                    .groupName("한화시스템")
                    .groupType(GroupType.DEPARTMENT)
                    .build();
            groupRepository.save(adminGroup);
        }
    }
}
