package com.pbook.book.exception;

public class BookAlreadyBorrowedException extends RuntimeException {
	public BookAlreadyBorrowedException(String message) {
		super(message);
	}
}
