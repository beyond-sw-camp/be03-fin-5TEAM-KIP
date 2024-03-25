package com.FINAL.KIP.user.service;

import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.dto.req.CreateUserReqDto;
import com.FINAL.KIP.user.dto.req.LoginReqDto;
import com.FINAL.KIP.user.dto.res.UserResDto;
import com.FINAL.KIP.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화

    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder ) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

//    Create
    public UserResDto createUser(CreateUserReqDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.makeUserReqDtoToUser().getPassword())); // 비밀번호 암호화
        User user = dto.makeUserReqDtoToUser();
        User savedUser = userRepo.save(user);
        return new UserResDto(savedUser);
    }

    public List<UserResDto> createUsers(List<CreateUserReqDto> dtos) {
        return dtos.stream()
                .map(CreateUserReqDto::makeUserReqDtoToUser)
                .map(userRepo::save)
                .map(UserResDto::new)
                .collect(Collectors.toList());
    }

//    Read
    public List<UserResDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(UserResDto::new)
                .collect(Collectors.toList());
    }
//    함수공통화
    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public User login(LoginReqDto loginReqDto) throws IllegalArgumentException{
//        사원번호 존재 여부 체크
        User user = userRepo.findByEmployeeId(loginReqDto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사원번호입니다."));
//        password 일치여부 // 일치하지 않으면 에러 터트림
        if(!passwordEncoder.matches(loginReqDto.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("비밀번호 불일치");
        }
        return user;
    }
}