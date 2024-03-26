package com.FINAL.KIP.request.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.group.domain.Group;
import com.FINAL.KIP.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Request extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDateTime dueDate;

	@Builder.Default
	private String isOk = "P";

	private int days;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Document documentId;

	@ManyToOne
	@JoinColumn(nullable = false)
	private User requesterId;

	@ManyToOne
	@JoinColumn(nullable = false)
	private Group groupId;

}