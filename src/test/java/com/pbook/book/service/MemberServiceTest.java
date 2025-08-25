package com.pbook.book.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pbook.book.entity.Member;
import com.pbook.book.enums.MemberType;
import com.pbook.book.exception.DuplicateEmailException;
import com.pbook.book.exception.InvalidEmailException;
import com.pbook.book.exception.MemberNotFoundException;
import com.pbook.book.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
	@Mock private MemberRepository memberRepository;
	@InjectMocks private MemberService memberService;



}
