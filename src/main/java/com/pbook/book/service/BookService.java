package com.pbook.book.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.pbook.book.entity.Book;
import com.pbook.book.exception.BookAlreadyBorrowedException;
import com.pbook.book.exception.BookNotFoundException;
import com.pbook.book.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
	private final BookRepository bookRepository;

	// 저자로 빌릴 수 있는 책 검색
	public List<Book> searchBooksByAuthor(String author) {
		if (author == null || author.trim().isEmpty()) {
			throw new IllegalArgumentException("Author name cannot be empty");
		}

		List<Book> books = bookRepository.findByAuthor(author);
		return books.stream()
			.filter(Book::isAvailable)
			.collect(Collectors.toList());
	}

	// 책 빌리기
	public void borrowBook(Long bookId) {
		Book book = bookRepository.findById(bookId)
			.orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));

		if (!book.isAvailable()) {
			throw new BookAlreadyBorrowedException("Book is already borrowed");
		}

		book.updateAvailable(false);
		bookRepository.save(book);
	}

	// 조건부 책 추천
	public String generateBookRecommendation(Book book) {
		if (book.isClassic()) {
			return "클래식 도서 추천: " + book.getDisplayName();
		} else if (2025 - book.getPublishYear() <= 5) {
			return "신간 도서 추천: " + book.getDisplayName();
		} else {
			return "일반 도서: " + book.getDisplayName();
		}
	}

	// 책 등록
	public boolean registerNewBook(String title, String author, int publishYear) {
		if (bookRepository.existsByTitleAndAuthor(title, author)) {
			return false; // 이미 존재
		}

		Book newBook = Book.builder()
				.title(title)
				.author(author)
				.publishYear(publishYear)
				.build(); // available은 기본값 true
		bookRepository.save(newBook);
		return true;
	}
}