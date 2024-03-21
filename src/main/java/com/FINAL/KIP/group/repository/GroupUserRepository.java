package com.FINAL.KIP.group.repository;

import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.group.domain.GroupUser;
import com.FINAL.KIP.group.domain.GroupUserId;
import com.FINAL.KIP.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupUserRepository extends JpaRepository<GroupUser, GroupUserId> {
    List<GroupUser> findByGroup(Group group);
    List<GroupUser> findByUser(User user);
    Optional<GroupUser> findByGroupAndUser(Group group, User user);
}
