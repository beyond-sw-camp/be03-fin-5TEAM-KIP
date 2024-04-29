package com.FINAL.KIP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;

@SpringBootApplication(exclude = {ElasticsearchDataAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class})
public class KipApplication {

	public static void main(String[] args) {
		SpringApplication.run(KipApplication.class, args);
	}

}
