package com.FINAL.KIP.note.repository;

import com.FINAL.KIP.note.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

}
