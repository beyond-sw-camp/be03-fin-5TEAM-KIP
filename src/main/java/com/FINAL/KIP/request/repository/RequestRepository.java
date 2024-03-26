package com.FINAL.KIP.request.repository;

import com.FINAL.KIP.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {

}
