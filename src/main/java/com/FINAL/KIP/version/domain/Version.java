package com.FINAL.KIP.version.domain;

import com.FINAL.KIP.common.domain.BaseEntity;
import com.FINAL.KIP.document.domain.Document;
import com.FINAL.KIP.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Version extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 10000)
	private String content;

	@Builder.Default
	private String message ="";

	@Builder.Default
	private String isShow = "Y";

	@ManyToOne(fetch = FetchType.LAZY)
	private Document document;

	@ManyToOne(fetch = FetchType.LAZY)
	private User writer;

	public void updateIsShow() {
		if (this.isShow.equals("Y")) {
			this.isShow = "N";
		} else {
			this.isShow = "Y";
		}
	}
}