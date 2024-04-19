package com.FINAL.KIP.common.aws.domain;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "document")
@Mapping(mappingPath = "opensearch/document-mapping.json")
@Setting(settingPath = "opensearch/document-setting.json")
public class EsDoc {

	@Id
	private String id;
	@Field(type = FieldType.Text)
	private String uuid;
	@Field(type = FieldType.Text)
	private String title;
	@Field(type = FieldType.Text)
	private String groupName;
	@Field(type = FieldType.Text)
	private String content;
}