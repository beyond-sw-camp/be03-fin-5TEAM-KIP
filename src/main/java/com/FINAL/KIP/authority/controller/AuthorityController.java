package com.FINAL.KIP.authority.controller;


import com.FINAL.KIP.authority.dto.req.CreateAuthorityGroupReqDto;
import com.FINAL.KIP.authority.dto.req.addUsersToGroupReqDto;
import com.FINAL.KIP.authority.dto.res.AuthorityGroupResDto;
import com.FINAL.KIP.authority.dto.res.GetAuthorityGroupHierarchyResDto;
import com.FINAL.KIP.authority.dto.res.GroupUsersRoleResDto;
import com.FINAL.KIP.authority.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("group")
public class AuthorityController {

    private final AuthorityService authorityService;

    @Autowired
    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    // Create
    @PostMapping
    public ResponseEntity<AuthorityGroupResDto> createAuthorityGroup(
            @RequestBody CreateAuthorityGroupReqDto dto) {
        return ResponseEntity.ok(
                authorityService.createAuthorityGroup(dto)
        );
    }
    @PostMapping("list")
    public ResponseEntity<List<AuthorityGroupResDto>> createAuthorityGroups(
            @RequestBody List<CreateAuthorityGroupReqDto> dtos) {
        return ResponseEntity.ok(
                authorityService.createAuthorityGroups(dtos));
    }


    // Read

    @GetMapping
    public ResponseEntity<List<AuthorityGroupResDto>> getAuthorityGroups(){
        return ResponseEntity.ok(
                authorityService.getAuthorityGroups()
        );
    }

    @GetMapping("childgroups/{groupId}")
    public ResponseEntity<List<AuthorityGroupResDto>> getAuthorityGroupChilds(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupsById(groupId)
        );
    }

    @GetMapping("hierarchy/{groupId}")
    public ResponseEntity<GetAuthorityGroupHierarchyResDto> getAuthorityGroupHierarchy(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupHierarchy(groupId)
        );
    }


// 그릅내 유저 추가하는 컨트롤러

//    Create
    @PostMapping("addusers")
    public ResponseEntity<List<GroupUsersRoleResDto>> addUsersToGroup(
            @RequestBody addUsersToGroupReqDto dto){
        return ResponseEntity.ok(
                authorityService.addUsersToGroup(dto)
        );
    }

    @PostMapping("addusers/list")
    public ResponseEntity<List<List<GroupUsersRoleResDto>>> addUsersToGroup(
            @RequestBody List<addUsersToGroupReqDto> dtos){
        return ResponseEntity.ok(
                authorityService.addUsersToGroupList(dtos)
        );
    }



//    Read
    @GetMapping("{groupId}/user")
    public ResponseEntity<List<GroupUsersRoleResDto>> getAuthorityGroupUsers(
            @PathVariable Long groupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupUsers(groupId)
        );
    }

}

