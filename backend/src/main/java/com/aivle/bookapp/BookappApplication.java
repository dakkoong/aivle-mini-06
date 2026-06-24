package com.aivle.bookapp;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.repository.BookRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class BookappApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(BookappApplication.class, args);
	}

	// 교안 p.126: 실습용 초기 데이터 5권 등록 (프론트엔드 맞춤 확장)
	@Bean
	public CommandLineRunner init(BookRepository repo) {
		return args -> {
			if (repo.count() == 0) {
				String now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			}
		};
	}

}
