package com.FINAL.KIP.user.controller;

import com.FINAL.KIP.user.dto.req.CreateUserReqDto;
import com.FINAL.KIP.user.dto.res.UserResDto;
import com.FINAL.KIP.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

    }


//    Create
    @PostMapping("create")
    public ResponseEntity<UserResDto> createUser(@RequestBody CreateUserReqDto dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PostMapping("create/users")
    public ResponseEntity<List<UserResDto>> createUsers(@RequestBody List<CreateUserReqDto> dtos){
        return ResponseEntity.ok(userService.createUsers(dtos));
    }

//    Read
    @GetMapping("all")
    public ResponseEntity<List<UserResDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
