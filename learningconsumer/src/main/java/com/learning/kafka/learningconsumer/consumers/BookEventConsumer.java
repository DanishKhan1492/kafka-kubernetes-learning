package com.learning.kafka.learningconsumer.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.kafka.learningconsumer.dao.BookDao;
import com.learning.kafka.learningconsumer.entity.Book;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookEventConsumer {

    private BookDao bookDao;
    private ObjectMapper objectMapper;

    public BookEventConsumer(BookDao bookDao){
        objectMapper = new ObjectMapper();
        this.bookDao = bookDao;
    }

    @KafkaListener(groupId = "create-books-event", topicPartitions = {@TopicPartition(topic = "create-books", partitions = {"1"})})
    public void consumeBookMessages(ConsumerRecord<Integer, String> bookEvent) throws JsonProcessingException {
        log.info("Create Book: Message Consumed From Kafka Partition: {} have Data: {}", 1, bookEvent.value());
        String book = objectMapper.readTree(bookEvent.value()).findValue("book").toString();
        bookDao.save(objectMapper.readValue(book, Book.class));
    }

    @KafkaListener(groupId = "update-books-event", topicPartitions = {@TopicPartition(topic = "create-books", partitions = {"0"})})
    public void consumeUpdateBookMessages(ConsumerRecord<Integer, String> bookEvent) throws JsonProcessingException {
        log.info("Update Book: Message Consumed From Kafka Partition: {} have Data: {}", 0, bookEvent.value());
        String book = objectMapper.readTree(bookEvent.value()).findValue("book").toString();
        bookDao.save(objectMapper.readValue(book, Book.class));
    }
}
