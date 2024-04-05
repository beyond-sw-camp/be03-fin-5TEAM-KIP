package com.FINAL.KIP.user.service;

import com.FINAL.KIP.bookmark.repository.BookRepository;
import com.FINAL.KIP.common.CommonResponse;
import com.FINAL.KIP.common.aspect.JustAdmin;
import com.FINAL.KIP.common.aspect.UserAdmin;
import com.FINAL.KIP.securities.JwtTokenProvider;
import com.FINAL.KIP.securities.refresh.UserRefreshToken;
import com.FINAL.KIP.securities.refresh.UserRefreshTokenRepository;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.dto.req.CreateUserReqDto;
import com.FINAL.KIP.user.dto.req.LoginReqDto;
import com.FINAL.KIP.user.dto.req.UserInfoUpdateReqDto;
import com.FINAL.KIP.user.dto.res.ProfileImageResDto;
import com.FINAL.KIP.user.dto.res.UserResDto;
import com.FINAL.KIP.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder; //비밀번호 암호화
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final BookRepository bookRepository;
    private final String uploadDir = "uploads/profiles"; // 프로필 이미지를 저장할 디렉토리 경로


    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserRefreshTokenRepository userRefreshTokenRepository, BookRepository bookRepository) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRefreshTokenRepository = userRefreshTokenRepository;
        this.bookRepository = bookRepository;
    }

    //    Create
    @UserAdmin
    public UserResDto createUser(CreateUserReqDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.makeUserReqDtoToUser().getPassword())); // 비밀번호 암호화
        User user = dto.makeUserReqDtoToUser();
        User savedUser = userRepo.save(user);
        return new UserResDto(savedUser);
    }

    @UserAdmin
    public List<UserResDto> createUsers(List<CreateUserReqDto> dtos) {
        for (CreateUserReqDto createUserReqDto : dtos) {
            createUserReqDto.setPassword(
                    passwordEncoder.encode(createUserReqDto.makeUserReqDtoToUser().getPassword()));
        }
        return dtos.stream()
                .map(CreateUserReqDto::makeUserReqDtoToUser)
                .map(userRepo::save)
                .map(UserResDto::new)
                .collect(Collectors.toList());
    }

    //    Read
    @JustAdmin
    public List<UserResDto> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(UserResDto::new)
                .collect(Collectors.toList());
    }

    @UserAdmin
    public User getUserById(Long id) {
        return userRepo.findById(id).orElse(null);
    }

    public CommonResponse login(LoginReqDto loginReqDto) throws IllegalArgumentException {
        User user = userRepo.findByEmployeeId(loginReqDto.getEmployeeId())
                .filter(
                        kip -> passwordEncoder.matches(loginReqDto.getPassword(), kip.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("사원번호 또는 비밀번호가 일치하지 않습니다."));

        String accessToken = jwtTokenProvider.createAccessToken(
                String.format("%s:%s", user.getEmployeeId(), user.getRole()));

        String refreshToken = jwtTokenProvider.createRefreshToken();
        userRefreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
                        kip -> kip.updateUserRefreshToken(refreshToken),
                        () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken))
                );
        Map<String, String> result = new HashMap<>();
        result.put("user_name", user.getName());
        result.put("access_token", accessToken);
        result.put("refresh_token", refreshToken);
        return new CommonResponse(HttpStatus.OK, "JWT token is created!", result);
    }

    @UserAdmin
    public CommonResponse mypage() {
        User userInfo = getUserFromAuthentication();
        return new CommonResponse(HttpStatus.OK, "User info loaded successfully!", userInfo);
    }

    @Transactional
    @UserAdmin
    public void update(UserInfoUpdateReqDto userInfoUpdateReqDto) {
        User userInfo = getUserFromAuthentication();
        userInfo.updateUserInfo(userInfoUpdateReqDto.getName(), userInfoUpdateReqDto.getEmail(),
                userInfoUpdateReqDto.getPhoneNumber());
    }

    @Transactional
    @UserAdmin
    public void delete(String employeeId) {
        User userInfo = getUserByEmployeeId(employeeId);
        userRepo.delete(userInfo);
    }

    //  함수 공통화
    @UserAdmin
    public User getUserFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByEmployeeId(authentication.getName());
    }

    @UserAdmin
    public User getUserByEmployeeId(String employeeId) {
        return userRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("사원번호를 찾을 수 없습니다. " + employeeId));
    }

    // 사용자 북마크 목록 조회
    public CommonResponse userBookList() {
        User userInfo = getUserFromAuthentication();
        String employeeId = userInfo.getEmployeeId();

        List<Object[]> bookList = bookRepository.findDocumentIdAndTitleByEmployeeId(employeeId);
        return new CommonResponse(HttpStatus.OK, "User Book List loaded successfully!", bookList);

    }


    // 현재 인증된 사용자 정보 조회
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = authentication.getName();
        return userRepo.findByEmployeeId(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("인증된 사용자를 찾을 수 없습니다."));
    }

    // 프로필 이미지 업로드
    @Transactional
    public ProfileImageResDto uploadProfileImage(MultipartFile file) throws IOException {
        User user = getAuthenticatedUser();

        String originalFilename = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        String storedFileName = UUID.randomUUID().toString() + fileExtension;

        Path destinationFile = Paths.get(uploadDir, originalFilename).toAbsolutePath().normalize();
        Files.createDirectories(destinationFile.getParent());

        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageUrl(destinationFile.toString());
        userRepo.save(user);

        return new ProfileImageResDto(user.getId(), user.getProfileImageUrl());
    }

    // 프로필 이미지 조회
    public String getProfileImageUrl() {
        User user = getAuthenticatedUser();
        return Optional.ofNullable(user.getProfileImageUrl())
                .orElseThrow(() -> new EntityNotFoundException("프로필 이미지가 설정되지 않았습니다."));
    }

    // 프로필 이미지 삭제
    @Transactional
    public String deleteProfileImage() {
        User user = getAuthenticatedUser();
        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            return "프로필 이미지가 없습니다!";
        } else {
            try {
                Path path = Paths.get(user.getProfileImageUrl());
                Files.deleteIfExists(path);
                user.setProfileImageUrl(null);
                userRepo.save(user);
                return "프로필 이미지가 성공적으로 삭제되었습니다.";
            } catch (IOException e) {
                return "프로필 이미지 삭제에 실패했습니다.";
            }
        }
    }

    // 프로필 이미지 업데이트
    @Transactional
    public ProfileImageResDto updateProfileImage(MultipartFile file) throws IOException {
        User user = getAuthenticatedUser();

        if (user.getProfileImageUrl() == null || user.getProfileImageUrl().isEmpty()) {
            throw new EntityNotFoundException("수정할 프로필 이미지가 없습니다. 프로필 이미지를 먼저 올려 주세요.");
        }

        // 기존 이미지 삭제
        Path oldImagePath = Paths.get(user.getProfileImageUrl());
        Files.deleteIfExists(oldImagePath);

        // 새 이미지 저장
        String originalFilename = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        String storedFileName = UUID.randomUUID() + fileExtension;

        Path destinationFile = Paths.get(uploadDir, originalFilename).toAbsolutePath().normalize();
        Files.createDirectories(destinationFile.getParent());
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        user.setProfileImageUrl(destinationFile.toString());
        userRepo.save(user);

        return new ProfileImageResDto(user.getId(), user.getProfileImageUrl());
    }
}