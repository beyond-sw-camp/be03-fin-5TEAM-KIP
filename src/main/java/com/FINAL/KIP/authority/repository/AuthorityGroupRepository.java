package com.FINAL.KIP.authority.repository;

import com.FINAL.KIP.authority.domain.AuthorityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityGroupRepository extends JpaRepository<AuthorityGroup, Long> {
}
