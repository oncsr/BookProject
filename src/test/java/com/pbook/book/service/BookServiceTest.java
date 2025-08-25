package com.pbook.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pbook.book.entity.Book;
import com.pbook.book.exception.BookAlreadyBorrowedException;
import com.pbook.book.exception.BookNotFoundException;
import com.pbook.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock private BookRepository bookRepository;
    
    @InjectMocks private BookService bookService;

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

    ///////////////////////////////////

    @Test
    @DisplayName("책이 없을 때 예외 발생")
    void borrowBook_책없음_예외발생() {
        // Given
        long bookId = -1;

        // When & Then
        assertThatThrownBy(() -> bookService.borrowBook(bookId))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Book not found: " + bookId);
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("빌릴 수 없는 책일 때 예외 발생")
    void borrowBook_빌릴수없는책_예외발생() {
        // Given
        long bookId = 1L;
        Book book = Book.builder().title("책1").author("author").publishYear(2020).available(false).build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When & Then
        assertThatThrownBy(() -> bookService.borrowBook(bookId))
            .isInstanceOf(BookAlreadyBorrowedException.class)
            .hasMessage("Book is already borrowed");
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("정상 대여")
    void borrowBook_정상대여() {
        // Given
        long bookId = 1L;
        Book book = Book.builder().title("책1").author("author").publishYear(2020).available(true).build();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // When
        bookService.borrowBook(bookId);

        // Then
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("클래식 도서 추천")
    void generateBookRecommendation_클래식도서추천() {
        // Given
        Book book = Book.builder().title("책1").author("author").publishYear(1945).available(true).build();

        // When
        String result = bookService.generateBookRecommendation(book);

        // Then
        assertThat(result).isEqualTo("클래식 도서 추천: " + book.getDisplayName());
    }

    @Test
    @DisplayName("신간 도서 추천")
    void generateBookRecommendation_신간도서추천() {
        // Given
        Book book = Book.builder().title("책1").author("author").publishYear(2023).available(true).build();

        // When
        String result = bookService.generateBookRecommendation(book);

        // Then
        assertThat(result).isEqualTo("신간 도서 추천: " + book.getDisplayName());
    }

    @Test
    @DisplayName("일반 도서 추천")
    void generateBookRecommendation_일반도서추천() {
        // Given
        Book book = Book.builder().title("책1").author("author").publishYear(2015).available(true).build();

        // When
        String result = bookService.generateBookRecommendation(book);

        // Then
        assertThat(result).isEqualTo("일반 도서: " + book.getDisplayName());
    }

    ///////////////////////////////////

    @Test
    @DisplayName("이미 존재하는 책 등록")
    void registerNewBook_이미존재하는책등록() {
        // Given
        String title = "제목1";
        String author = "저자1";
        int publishYear = 2020;
        when(bookRepository.existsByTitleAndAuthor(title, author)).thenReturn(true);

        // When
        boolean result = bookService.registerNewBook(title, author, publishYear);

        // Then
        assertThat(result).isFalse();
        verify(bookRepository, times(1)).existsByTitleAndAuthor(title, author);
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("책 정상 등록")
    void registerNewBook_책정상등록() {
        // Given
        String title = "제목1";
        String author = "저자1";
        int publishYear = 2020;
        when(bookRepository.existsByTitleAndAuthor(title, author)).thenReturn(false);

        // When
        boolean result = bookService.registerNewBook(title, author, publishYear);

        // Then
        assertThat(result).isTrue();
        verify(bookRepository, times(1)).existsByTitleAndAuthor(title, author);
        verify(bookRepository, times(1)).save(any());
    }

}