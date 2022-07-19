package com.learning.kafka.learningconsumer.dao;

import com.learning.kafka.learningconsumer.entity.Book;
import org.springframework.data.repository.CrudRepository;

public interface BookDao extends CrudRepository<Book, Integer> {
}
