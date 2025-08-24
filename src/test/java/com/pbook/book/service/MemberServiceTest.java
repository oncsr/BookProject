package com.pbook.book.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pbook.book.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@Mock private BookRepository bookRepository;
	@InjectMocks private MemberService memberService;


}
