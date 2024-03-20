package com.FINAL.KIP.user.repository;


import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.user.domain.User;
import com.FINAL.KIP.user.domain.UserDocAuthority;
import com.FINAL.KIP.user.domain.UserDocAuthorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDocAuthorityRepository extends JpaRepository<UserDocAuthority, UserDocAuthorityId> {
    List<UserDocAuthority> findByUser(User user);
    List<UserDocAuthority> findByDocument(Document document);

    Optional<UserDocAuthority> findByUserAndDocument(User user, Document document);
}
