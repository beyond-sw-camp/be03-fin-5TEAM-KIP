package com.FINAL.KIP.common;

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

public InitialDataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
}

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmployeeId("admin@test.com").isEmpty()) {
            User adminMember = User.builder()
                    .name("admin")
                    .email("admin@test.com")
                    .password(passwordEncoder.encode("1234"))
                    .phoneNumber("01012345678")
                    .employeeId("KK")
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(adminMember);
        }
    }
}
