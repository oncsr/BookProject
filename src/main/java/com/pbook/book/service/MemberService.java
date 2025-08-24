package com.pbook.book.service;

import org.springframework.stereotype.Service;

import com.pbook.book.entity.Member;
import com.pbook.book.enums.MemberType;
import com.pbook.book.exception.DuplicateEmailException;
import com.pbook.book.exception.InvalidEmailException;
import com.pbook.book.exception.MemberNotFoundException;
import com.pbook.book.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	// 이메일 검증 로직 (단위 테스트용)
	public boolean isValidEmail(String email) {
		if (email == null) return false;
		return email.contains("@") && email.contains(".");
	}

	// 회원 등록 (예외 상황 테스트용)
	public Member registerMember(String name, String email, MemberType type) {
		if (!isValidEmail(email)) {
			throw new InvalidEmailException("Invalid email format");
		}

		if (memberRepository.existsByEmail(email)) {
			throw new DuplicateEmailException("Email already exists");
		}

		Member member = new Member(name, email, type);
		memberRepository.save(member);
		return member;
	}

	// 멤버십 업그레이드 로직
	public boolean upgradeMembership(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new MemberNotFoundException("Member not found"));

		if (member.getType() == MemberType.GUEST) {
			member.updateMemberType(MemberType.REGULAR);
			memberRepository.save(member);
			return true;
		} else if (member.getType() == MemberType.REGULAR) {
			member.updateMemberType(MemberType.PREMIUM);
			memberRepository.save(member);
			return true;
		}

		return false; // 이미 PREMIUM
	}
}
