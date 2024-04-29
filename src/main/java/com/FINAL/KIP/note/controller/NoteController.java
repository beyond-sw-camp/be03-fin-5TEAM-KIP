package com.FINAL.KIP.note.controller;

import com.FINAL.KIP.note.dto.response.NoteGetReqDto;
import com.FINAL.KIP.note.service.NoteService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class NoteController {

	private final NoteService noteService;

	@Autowired
	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	@GetMapping("")
	public ResponseEntity<List<NoteGetReqDto>> getAllNote() {
		return noteService.getAllNote();
	}

	@DeleteMapping("/{noteId}")
	public ResponseEntity<String> deleteNote(@PathVariable Long noteId) {
		return noteService.removeNote(noteId);
	}

	@GetMapping("/{noteId}")
	public ResponseEntity<Long> readNote(@PathVariable Long noteId) {
		return noteService.readNote(noteId);
	}
}