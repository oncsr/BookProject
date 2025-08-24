package com.pbook.book.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "books")
@NoArgsConstructor
@Getter
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false)
	private String author;

	@Column(name = "publish_year")
	private int publishYear;

	@Column(nullable = false)
	private boolean available;

	@Builder
	public Book(String title, String author, int publishYear, Boolean available) {
		this.title = title;
		this.author = author;
		this.publishYear = publishYear;
		this.available = available != null ? available : true; // 기본값 true
	}

	public void updateAvailable(boolean available) { this.available = available; }

	public boolean isClassic() {
		return publishYear < 1950;
	}

	public String getDisplayName() {
		return title + " (" + author + ")";
	}
}