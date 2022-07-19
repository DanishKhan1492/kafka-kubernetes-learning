package com.learning.library.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learning.library.dto.Book;
import com.learning.library.dto.LibraryEvent;
import com.learning.library.producers.BookEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class BookController {
	
	@Autowired
	private BookEventProducer bookEventProducer;
	
	@PostMapping("/add")
	public ResponseEntity<String> addBook(@RequestBody Book book) throws JsonProcessingException{
		var libraryEvent = new LibraryEvent(new Random(200000).nextInt(), book);
		bookEventProducer.sendBookEvent(libraryEvent);
		return new ResponseEntity<>("Book Created", HttpStatus.CREATED);
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateBook(@RequestBody @Valid LibraryEvent libraryEvent) throws JsonProcessingException{
		bookEventProducer.sendUpdateBookEvent(libraryEvent);
		return new ResponseEntity<>("Book Updated", HttpStatus.OK);
	}

}
