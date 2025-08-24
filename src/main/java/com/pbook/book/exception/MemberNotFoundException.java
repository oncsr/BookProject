package com.pbook.book.exception;

public class MemberNotFoundException extends RuntimeException {
	public MemberNotFoundException(String message) {
		super(message);
	}
}
