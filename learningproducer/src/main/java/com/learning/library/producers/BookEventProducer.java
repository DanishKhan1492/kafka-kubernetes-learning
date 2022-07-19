package com.learning.library.producers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.library.dto.LibraryEvent;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BookEventProducer {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    public void sendBookEvent(LibraryEvent libraryEvent) throws JsonProcessingException{
        log.info("Create Book Event With Data: {}", libraryEvent);
        kafkaTemplate.send("create-books", 1 ,libraryEvent.libraryEventId(), new ObjectMapper().writeValueAsString(libraryEvent));
    }

    public void sendUpdateBookEvent(LibraryEvent libraryEvent) throws JsonProcessingException{
        log.info("Update Book Event With Data: {}", libraryEvent);
        kafkaTemplate.send("create-books", 0 ,libraryEvent.libraryEventId(), new ObjectMapper().writeValueAsString(libraryEvent));
    }
	
}
