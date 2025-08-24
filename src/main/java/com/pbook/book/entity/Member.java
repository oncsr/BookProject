package com.pbook.book.entity;

import com.pbook.book.enums.MemberType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "members")
@NoArgsConstructor
@Getter
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true, nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MemberType type;

	@Builder
	public Member(String name, String email, MemberType type) {
		this.name = name;
		this.email = email;
		this.type = type;
	}

	public boolean canBorrowBook() {
		return type == MemberType.PREMIUM || type == MemberType.REGULAR;
	}

	public void updateMemberType(MemberType memberType) { this.type = memberType; }
}