package com.FINAL.KIP.user.controller;

import com.FINAL.KIP.common.CommonResponse;
import com.FINAL.KIP.common.firebase.service.FCMService;
import com.FINAL.KIP.securities.JwtTokenProvider;
import com.FINAL.KIP.user.dto.req.CreateUserReqDto;
import com.FINAL.KIP.user.dto.req.LoginReqDto;
import com.FINAL.KIP.user.dto.req.PasswordChangeRequest;
import com.FINAL.KIP.user.dto.req.UserInfoUpdateReqDto;
import com.FINAL.KIP.user.dto.res.UserResDto;
import com.FINAL.KIP.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FCMService fcmService;

    @Autowired
    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider,
                          FCMService fcmService) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.fcmService = fcmService;
    }

    //    Create
    @PostMapping
    public ResponseEntity<UserResDto> createUser(@RequestBody CreateUserReqDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("list")
    public ResponseEntity<List<UserResDto>> createUsers(@RequestBody List<CreateUserReqDto> dtos) {
        return ResponseEntity.ok(userService.createUsers(dtos));
    }

    //    Read
    @GetMapping
    public ResponseEntity<List<UserResDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // [중복체크] 아이디 DB에 존재하는지 여부 리턴
    @GetMapping("{employeeId}/id")
    public ResponseEntity<Boolean> checkIfEmployeeIdExists(@PathVariable String employeeId) {
        return ResponseEntity.ok(userService.checkIfEmployeeIdExists(employeeId));
    }

    // [비번체크] 아이디와 패스워드가 일치하면 회원 이름만 리턴
    @PostMapping("check")
    public ResponseEntity<Map<String, Object>> checkIdPassAndReturnName(@RequestBody LoginReqDto dto) {
        return ResponseEntity.ok(userService.checkIdPassAndReturnName(dto));
    }

    // [중복체크] 휴대폰 아이디가 DB에 존재하는지 여부 리턴
    @GetMapping("{phoneNumber}/phone")
    public ResponseEntity<Boolean> checkIfPhoneNumberExists(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(userService.checkIfPhoneNumberExists(phoneNumber));
    }

    // [중복체크] 이메일이 DB에 존재하는지 여부 리턴
    @GetMapping("{email}/email")
    public ResponseEntity<Boolean> checkIfEmailExists(@PathVariable String email) {
        return ResponseEntity.ok(userService.checkIfEmailExists(email));
    }


    // 로그인
    @PostMapping("login") //login은 토큰 사용으로 Map형식으로 받아주어야함 // Map<String, Object>
    public ResponseEntity<CommonResponse> userLogin(@RequestBody LoginReqDto loginReqDto) {
        CommonResponse commonResponse = userService.login(loginReqDto);
        if (loginReqDto.getToken() != null)
            fcmService.saveToken(loginReqDto);
        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }

    // 로그아웃
    @DeleteMapping("logout")
    public ResponseEntity<CommonResponse> userLogout() {
        CommonResponse commonResponse = userService.logout();
        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }


    // 사용자 마이페이지
    @GetMapping("mypage")
    public ResponseEntity<CommonResponse> myPage() {
        CommonResponse commonResponse = userService.mypage();
        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }

    // 사용자 정보 업데이트
    @PatchMapping
    public ResponseEntity<CommonResponse> userUpdate(@RequestBody UserInfoUpdateReqDto userInfoUpdateReqDto) {
        userService.update(userInfoUpdateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "User updated successfully", userInfoUpdateReqDto.getName()), HttpStatus.OK);
    }


    // 사용자 회원 삭제
    @DeleteMapping("{employeeId}")
    public ResponseEntity<CommonResponse> userDelete(@PathVariable(value = "employeeId") String employeeId) {
        userService.delete(employeeId);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "User deleted successfully", employeeId), HttpStatus.OK);
    }

    // 비밀번호 변경
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            boolean passwordChanged = userService.changePassword(
                    request.getFindByEmployeeId(),
                    request.getCurrentPassword(),
                    request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Password successfully updated."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to change password due to an unexpected error."));
        }
    }

    // 프로필이미지 업로드
    @PostMapping("/profile/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String originName = file.getOriginalFilename();
            String fileUrl = userService.uploadImage(file);
            return ResponseEntity.ok(Map.of(
                    "uploaded", true,
                    "originName", originName,
                    "url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 프로필이미지 조회
    @GetMapping("/profile/view")
    public ResponseEntity<?> viewProfileImage() {
        try {
            String profileImageUrl = userService.getProfileImageUrl();
            return ResponseEntity.ok(Map.of("message", "프로필 이미지 URL 조회 성공", "profileImageUrl", profileImageUrl));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }


//    // 프로필 이미지 업로드
//    @PostMapping("/profile/upload")
//    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "프로필 이미지 업로드에 오류가 발생하였습니다: 이미지가 존재하지 않습니다."));
//        }
//        try {
//            ProfileImageResDto profileImageResDto = userService.uploadProfileImage(file);
//            return ResponseEntity.ok(Map.of("message", "프로필 이미지가 성공적으로 업로드 되었습니다.", "profileImageUrl", profileImageResDto.getProfileImageUrl()));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "이미지 업로드에 실패했습니다: " + e.getMessage()));
//        }
//    }
//
//    // 프로필 이미지 조회
//    @GetMapping("/profile/view")
//    public ResponseEntity<?> viewProfileImage() {
//        try {
//            String profileImageUrl = userService.getProfileImageUrl();
//            return ResponseEntity.ok(Map.of("message", "프로필 이미지 URL 조회 성공", "profileImageUrl", profileImageUrl));
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
//        }
//    }
//
//    // 프로필 이미지 삭제
//    @DeleteMapping("/profile/delete")
//    public ResponseEntity<?> deleteProfileImage() {
//        String message = userService.deleteProfileImage();
//        if (message.equals("프로필 이미지가 성공적으로 삭제되었습니다.")) {
//            return ResponseEntity.ok(Map.of("message", message));
//        } else {
//            return ResponseEntity.badRequest().body(Map.of("message", message));
//        }
//    }
//
//    // 프로필 이미지 업데이트
//    @PatchMapping("/profile/updateImage")
//    public ResponseEntity<?> updateProfileImage(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "프로필 이미지 변경에 오류가 발생하였습니다: 이미지가 존재하지 않습니다."));
//        }
//        try {
//            ProfileImageResDto profileImageResDto = userService.updateProfileImage(file);
//            return ResponseEntity.ok(Map.of("message", "프로필 이미지가 성공적으로 업데이트 되었습니다.", "profileImageUrl", profileImageResDto.getProfileImageUrl()));
//        } catch (EntityNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "프로필 이미지 업데이트에 실패했습니다: " + e.getMessage()));
//        }
//    }


}