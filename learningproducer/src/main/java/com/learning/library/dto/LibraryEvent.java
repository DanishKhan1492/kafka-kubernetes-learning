package com.learning.library.dto;

import jakarta.validation.constraints.NotNull;

public record LibraryEvent(@NotNull(message = "Library Event ID must not be null") Integer libraryEventId, Book book) {}
