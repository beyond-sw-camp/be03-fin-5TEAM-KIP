package com.FINAL.KIP.document.controller;

import com.FINAL.KIP.document.service.DocumentSearchService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doc/search")
public class DocumentSearchController {

	private final DocumentSearchService documentSearchService;

	@Autowired
	public DocumentSearchController(DocumentSearchService documentSearchService) {
		this.documentSearchService = documentSearchService;
	}

	@GetMapping("/test")
	public ResponseEntity<?> test() throws IOException {
		return new ResponseEntity<>(documentSearchService.test(), HttpStatus.OK);
	}

	@GetMapping("/addAll")
	public void addAll() {
		documentSearchService.addAll();
	}

	@GetMapping("")
	public ResponseEntity<?> searchDocs(@RequestParam String keyword, @RequestParam int pageNumber) {
		return documentSearchService.searchDocs(keyword, pageNumber);
	}

	@GetMapping("/{documentUUID}")
	public ResponseEntity<?> canView(@PathVariable String documentUUID) {
		return documentSearchService.viewDocs(documentUUID);
	}

}