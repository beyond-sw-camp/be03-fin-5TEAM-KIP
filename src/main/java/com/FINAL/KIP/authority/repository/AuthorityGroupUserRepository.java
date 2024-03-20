package com.FINAL.KIP.authority.repository;

import com.FINAL.KIP.authority.domain.AuthorityGroup;
import com.FINAL.KIP.authority.domain.AuthorityGroupUser;
import com.FINAL.KIP.authority.domain.AuthorityGroupUserId;
import com.FINAL.KIP.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorityGroupUserRepository extends JpaRepository<AuthorityGroupUser, AuthorityGroupUserId> {
    List<AuthorityGroupUser> findByAuthorityGroup(AuthorityGroup authorityGroup);
    List<AuthorityGroupUser> findByUser(User user);
    Optional<AuthorityGroupUser> findByAuthorityGroupAndUser(AuthorityGroup authorityGroup, User user);
}
