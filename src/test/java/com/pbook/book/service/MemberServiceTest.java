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

	@Test
	@DisplayName("이메일이 null인 경우 false 반환")
	void isValidEmail_이메일이null() {
		// Given
		String email = null;

		// When
		boolean result = memberService.isValidEmail(email);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("이메일에 @이 포함되지 않은 경우 false 반환")
	void isValidEmail_이메일골뱅이미포함() {
		// Given
		String email = "ssafy.123";

		// When
		boolean result = memberService.isValidEmail(email);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("이메일에 .이 포함되지 않은 경우 false 반환")
	void isValidEmail_이메일점미포함() {
		// Given
		String email = "ssafy@123";

		// When
		boolean result = memberService.isValidEmail(email);

		// Then
		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("정상적인 이메일")
	void isValidEmail_이메일정상() {
		// Given
		String email = "ssafy@naver.com";

		// When
		boolean result = memberService.isValidEmail(email);

		// Then
		assertThat(result).isTrue();
	}

	////////////////////////////////////////

	@Test
	@DisplayName("잘못된 이메일 형식으로 회원 등록 시 예외 발생")
	void registerMember_잘못된이메일형식_예외발생() {
		// Given
		String name = "김회원";
		String email = "invalid-email";
		MemberType type = MemberType.REGULAR;

		// When & Then
		assertThatThrownBy(() -> memberService.registerMember(name, email, type))
			.isInstanceOf(InvalidEmailException.class)
			.hasMessage("Invalid email format");
		verify(memberRepository, never()).existsByEmail(any());
		verify(memberRepository, never()).save(any());
	}

	@Test
	@DisplayName("중복 이메일로 회원 등록 시 예외 발생")
	void registerMember_중복이메일_예외발생() {
		// Given
		String name = "김회원";
		String email = "test@naver.com";
		MemberType type = MemberType.REGULAR;
		when(memberRepository.existsByEmail(email)).thenReturn(true);

		// When & Then
		assertThatThrownBy(() -> memberService.registerMember(name, email, type))
			.isInstanceOf(DuplicateEmailException.class)
			.hasMessage("Email already exists");
		verify(memberRepository, times(1)).existsByEmail(email);
		verify(memberRepository, never()).save(any());
	}

	@Test
	@DisplayName("정상적인 회원 등록")
	void registerMember_정상등록() {
		// Given
		String name = "김회원";
		String email = "test@naver.com";
		MemberType type = MemberType.REGULAR;
		when(memberRepository.existsByEmail(email)).thenReturn(false);

		// When
		Member result = memberService.registerMember(name, email, type);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getType()).isEqualTo(type);
		verify(memberRepository, times(1)).existsByEmail(email);
		verify(memberRepository, times(1)).save(any(Member.class));
	}

	////////////////////////////////////////

	@Test
	@DisplayName("존재하지 않는 회원 ID로 멤버십 업그레이드 시 예외 발생")
	void upgradeMembership_존재하지않는회원_예외발생() {
		// Given
		Long memberId = 999L;
		when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> memberService.upgradeMembership(memberId))
			.isInstanceOf(MemberNotFoundException.class)
			.hasMessage("Member not found");
		verify(memberRepository, times(1)).findById(memberId);
		verify(memberRepository, never()).save(any());
	}

	@Test
	@DisplayName("GUEST 회원의 멤버십 업그레이드 - REGULAR로 변경")
	void upgradeMembership_GUEST회원_REGULAR로업그레이드() {
		// Given
		Long memberId = 1L;
		Member member = new Member("김회원", "test@naver.com", MemberType.GUEST);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		// When
		boolean result = memberService.upgradeMembership(memberId);

		// Then
		assertThat(result).isTrue();
		assertThat(member.getType()).isEqualTo(MemberType.REGULAR);
		verify(memberRepository, times(1)).findById(memberId);
		verify(memberRepository, times(1)).save(member);
	}

	@Test
	@DisplayName("REGULAR 회원의 멤버십 업그레이드 - PREMIUM으로 변경")
	void upgradeMembership_REGULAR회원_PREMIUM으로업그레이드() {
		// Given
		Long memberId = 1L;
		Member member = new Member("김회원", "test@naver.com", MemberType.REGULAR);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		// When
		boolean result = memberService.upgradeMembership(memberId);

		// Then
		assertThat(result).isTrue();
		assertThat(member.getType()).isEqualTo(MemberType.PREMIUM);
		verify(memberRepository, times(1)).findById(memberId);
		verify(memberRepository, times(1)).save(member);
	}

	@Test
	@DisplayName("PREMIUM 회원의 멤버십 업그레이드 - 이미 최고 등급이므로 false 반환")
	void upgradeMembership_PREMIUM회원_업그레이드불가() {
		// Given
		Long memberId = 1L;
		Member member = new Member("김회원", "test@naver.com", MemberType.PREMIUM);
		when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

		// When
		boolean result = memberService.upgradeMembership(memberId);

		// Then
		assertThat(result).isFalse();
		assertThat(member.getType()).isEqualTo(MemberType.PREMIUM); // 변경되지 않음
		verify(memberRepository, times(1)).findById(memberId);
		verify(memberRepository, never()).save(any());
	}

}
