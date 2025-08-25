package com.pbook.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pbook.book.entity.Book;
import com.pbook.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookService bookService;
    
    
    @Test
    @DisplayName("정상적인 저자명으로 검색 - 빌릴 수 있는 책만 반환")
    void searchBooksByAuthor_정상검색_빌릴수있는책만반환() {
        // Given
        String author = "김작가";
        List<Book> allBooks = List.of(
            Book.builder().title("책1").author(author).publishYear(2020).available(true).build(),   // 빌릴 수 있음
            Book.builder().title("책2").author(author).publishYear(2021).available(false).build(),  // 이미 빌린 책
            Book.builder().title("책3").author(author).publishYear(2022).available(true).build()    // 빌릴 수 있음
        );
        
        when(bookRepository.findByAuthor(author)).thenReturn(allBooks);
        
        // When
        List<Book> result = bookService.searchBooksByAuthor(author);
        
        // Then
        assertThat(result)
                .hasSize(2) // 빌릴 수 있는 책만 2권
                .allMatch(Book::isAvailable);
        verify(bookRepository).findByAuthor(author);
    }
    
    @Test
    @DisplayName("저자명이 null일 때 예외 발생")
    void searchBooksByAuthor_저자명null_예외발생() {
        // Given
        String author = null;
        
        // When & Then
        assertThatThrownBy(() -> bookService.searchBooksByAuthor(author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Author name cannot be empty");
        verify(bookRepository, never()).findByAuthor(any());
    }
    
    @Test
    @DisplayName("저자명이 빈 문자열일 때 예외 발생")
    void searchBooksByAuthor_저자명빈문자열_예외발생() {
        // Given
        String author = "   "; // 공백만 있는 문자열
        
        // When & Then
        assertThatThrownBy(() -> bookService.searchBooksByAuthor(author))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Author name cannot be empty");

        verify(bookRepository, never()).findByAuthor(any());
    }
    
    @Test
    @DisplayName("해당 저자의 책이 없을 때 빈 리스트 반환")
    void searchBooksByAuthor_책없음_빈리스트반환() {
        // Given
        String author = "없는작가";
        when(bookRepository.findByAuthor(author)).thenReturn(Collections.emptyList());
        
        // When
        List<Book> result = bookService.searchBooksByAuthor(author);
        
        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findByAuthor(author);
    }
    
    @Test
    @DisplayName("모든 책이 빌려진 상태일 때 빈 리스트 반환")
    void searchBooksByAuthor_모든책빌려짐_빈리스트반환() {
        // Given
        String author = "인기작가";
        List<Book> allBorrowedBooks = List.of(
            Book.builder().title("책1").author(author).publishYear(2020).available(false).build(),
            Book.builder().title("책2").author(author).publishYear(2021).available(false).build()
        );
        
        when(bookRepository.findByAuthor(author)).thenReturn(allBorrowedBooks);
        
        // When
        List<Book> result = bookService.searchBooksByAuthor(author);
        
        // Then
        assertThat(result).isEmpty();
        verify(bookRepository).findByAuthor(author);
    }
}