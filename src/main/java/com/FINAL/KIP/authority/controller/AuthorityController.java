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
@RequestMapping("authority")
public class AuthorityController {

    private final AuthorityService authorityService;

    @Autowired
    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }


    // Create
    @PostMapping("create")
    public ResponseEntity<AuthorityGroupResDto> createAuthorityGroup(
            @RequestBody CreateAuthorityGroupReqDto dto) {
        return ResponseEntity.ok(
                authorityService.createAuthorityGroup(dto)
        );
    }

    @PostMapping("create/groups")
    public ResponseEntity<List<AuthorityGroupResDto>> createAuthorityGroups(
            @RequestBody List<CreateAuthorityGroupReqDto> dtos) {
        return ResponseEntity.ok(
                authorityService.createAuthorityGroups(dtos));
    }

    @PostMapping("addusers")
    public ResponseEntity<List<GroupUsersRoleResDto>> addUsersToGroup(
            @RequestBody addUsersToGroupReqDto dto){
        return ResponseEntity.ok(
                authorityService.addUsersToGroup(dto)
        );
    }

    @PostMapping("addusers/groups")
    public ResponseEntity<List<List<GroupUsersRoleResDto>>> addUsersToGroup(
            @RequestBody List<addUsersToGroupReqDto> dtos){
        return ResponseEntity.ok(
                authorityService.addUsersToGroupList(dtos)
        );
    }

    // Read
    @GetMapping("childgroups/{authorityGroupId}")
    public ResponseEntity<List<AuthorityGroupResDto>> getAuthorityGroupChilds(
            @PathVariable Long authorityGroupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupsById(authorityGroupId)
        );
    }

    @GetMapping("hierarchy/{authorityGroupId}")
    public ResponseEntity<GetAuthorityGroupHierarchyResDto> getAuthorityGroupHierarchy(
            @PathVariable Long authorityGroupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupHierarchy(authorityGroupId)
        );
    }

    @GetMapping("getusers/{authorityGroupId}")
    public ResponseEntity<List<GroupUsersRoleResDto>> getAuthorityGroupUsers(
            @PathVariable Long authorityGroupId) {
        return ResponseEntity.ok(
                authorityService.getAuthorityGroupUsers(authorityGroupId)
        );
    }

}

