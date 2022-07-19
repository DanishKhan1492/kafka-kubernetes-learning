package com.learning.kafka.learningconsumer.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer bookId;

    private String bookName;

    private String bookAuthor;
}
