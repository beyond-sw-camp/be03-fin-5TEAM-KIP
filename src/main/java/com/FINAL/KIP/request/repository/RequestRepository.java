package com.FINAL.KIP.request.repository;

import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.request.domain.Request;
import com.FINAL.KIP.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestRepository extends JpaRepository<Request, Long> {

	Boolean existsRequestByRequesterAndDocumentAndIsOk(User requester, Document document, String isOk);

	List<Request> findRequestByRequesterAndRequesterDelYn(User request, String delYn);

	List<Request> findRequestByGroupAndDelYn(Group group, String delYn);

	@Query("SELECT r FROM Request r WHERE r.dueDate > CURRENT_TIMESTAMP AND r.requester = :user")
	List<Request> findAgreedRequest(@Param("user") User user);

	@Query("SELECT COUNT(r) > 0 FROM Request r WHERE r.dueDate > CURRENT_TIMESTAMP AND r.requester = :user AND r.document = :document")
	boolean availableDocument(@Param("user") User user, @Param("document") Document document);
}
