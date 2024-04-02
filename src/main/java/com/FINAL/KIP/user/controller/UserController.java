package com.FINAL.KIP.user.controller;

import com.FINAL.KIP.common.CommonResponse;
import com.FINAL.KIP.common.firebase.service.FCMService;
import com.FINAL.KIP.securities.JwtTokenProvider;
import com.FINAL.KIP.user.dto.req.CreateUserReqDto;
import com.FINAL.KIP.user.dto.req.LoginReqDto;
import com.FINAL.KIP.user.dto.req.UserInfoUpdateReqDto;
import com.FINAL.KIP.user.dto.res.UserResDto;
import com.FINAL.KIP.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<UserResDto>> createUsers(@RequestBody List<CreateUserReqDto> dtos){
        return ResponseEntity.ok(userService.createUsers(dtos));
    }

//    Read
    @GetMapping
    public ResponseEntity<List<UserResDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 로그인
    @PostMapping("login") //login은 토큰 사용으로 Map형식으로 받아주어야함 // Map<String, Object>
    public ResponseEntity<CommonResponse> userLogin(@RequestBody LoginReqDto loginReqDto){
        CommonResponse commonResponse = userService.login(loginReqDto);
        if(loginReqDto.getToken()!=null)
            fcmService.saveToken(loginReqDto);
        return new ResponseEntity<>(commonResponse , HttpStatus.OK);
    }

    // 사용자 마이페이지
    @GetMapping("mypage")
    public ResponseEntity<CommonResponse> myPage() {
        CommonResponse commonResponse = userService.mypage();
        return new ResponseEntity<>(commonResponse, HttpStatus.OK);
    }

    // 사용자 정보 업데이트
    @PatchMapping
    public ResponseEntity<CommonResponse> userUpdate(@RequestBody UserInfoUpdateReqDto userInfoUpdateReqDto){
        userService.update(userInfoUpdateReqDto);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "User updated successfully", userInfoUpdateReqDto.getName()), HttpStatus.OK);
    }

    // 사용자 회원 삭제
    @DeleteMapping("{employeeId}")
    public ResponseEntity<CommonResponse> userDelete(@PathVariable(value = "employeeId") String employeeId){
        userService.delete(employeeId);
        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "User deleted successfully", employeeId), HttpStatus.OK);
    }

    @GetMapping("/book/list")
    public ResponseEntity<CommonResponse> userBookList(){
        CommonResponse commonResponse = userService.userBookList();
        return new ResponseEntity<>(commonResponse , HttpStatus.OK);
    }


//        User user = userService.login(loginReqDto);
////        토큰 생성
//        String jwtToken = jwtTokenProvider.createToken(user.getEmployeeId(), user.getRole().toString());
//        Map<String, Object> user_info = new HashMap<>();
//        user_info.put("id", user.getId());
//        user_info.put("employeeId", user.getEmployeeId());
//        user_info.put("token", jwtToken);
//        return new ResponseEntity<>(new CommonResponse(HttpStatus.OK, "user successfully login", user_info), HttpStatus.OK);
//    }
}
