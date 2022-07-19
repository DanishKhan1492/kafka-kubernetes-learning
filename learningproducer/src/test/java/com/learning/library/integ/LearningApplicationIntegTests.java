package com.learning.library.integ;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.learning.library.dto.LibraryEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.library.dto.Book;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"create-books"}, partitions = 3)
@TestPropertySource(properties = {"spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
"spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
class LearningApplicationIntegTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<Integer, String> bookConsumer;

    @BeforeEach
    void initializeConsumer(){
        Map<String, Object> configs =new HashMap<>(KafkaTestUtils.consumerProps("create-books-group", "true", embeddedKafkaBroker));
        bookConsumer = new DefaultKafkaConsumerFactory<>(configs, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(bookConsumer);
    }

    @AfterEach
    void closeConsumer(){
        bookConsumer.close();
    }

    @Test
    void testAddBookProducer() throws JsonProcessingException{
        Book book = new Book(234, "A Case Of Exploding Mangos", "Muhammad Hanif");
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<Book> request = new HttpEntity<>(book, headers);
        ResponseEntity<String> result = testRestTemplate.exchange("/add", HttpMethod.POST, request, String.class);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());


        ConsumerRecord<Integer, String> bookRecord = KafkaTestUtils.getSingleRecord(bookConsumer, "create-books");
        String value = bookRecord.value();

        String bookFromResponse = new ObjectMapper().readTree(value).findValue("book").toString();

        assertEquals(new ObjectMapper().writeValueAsString(book), bookFromResponse);
    }

    @Test
    void testUpdateBookProducer() throws JsonProcessingException{
        Book book = new Book(234, "A Case Of Exploding Mangoes", "Muhammad Hanif");
        LibraryEvent libraryEvent = new LibraryEvent(new Random(200000).nextInt(), book);
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);
        ResponseEntity<String> result = testRestTemplate.exchange("/update", HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.OK, result.getStatusCode());


        ConsumerRecord<Integer, String> bookRecord = (ConsumerRecord<Integer, String>) KafkaTestUtils.getOneRecord(embeddedKafkaBroker.getBrokersAsString(),"update-books-group", "create-books",0, true, true, 60000);
        String value = bookRecord.value();

        String bookFromResponse = new ObjectMapper().readTree(value).findValue("book").toString();

        assertEquals(new ObjectMapper().writeValueAsString(book), bookFromResponse);
    }


}
