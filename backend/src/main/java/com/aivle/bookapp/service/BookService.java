package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.Book;
import com.aivle.bookapp.domain.Likes;
import com.aivle.bookapp.domain.User;
import com.aivle.bookapp.exception.BookAlreadyExistsException;
import com.aivle.bookapp.exception.BookNotFoundException;
import com.aivle.bookapp.repository.AiRecommendationRepository;
import com.aivle.bookapp.repository.BookRepository;
import com.aivle.bookapp.repository.LikeRepository;
import com.aivle.bookapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final AiRecommendationService aiRecommendationService;

    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book create(Book book) {
        String now = now();
        if (book.getCreatedAt() == null) {
            book.setCreatedAt(now);
        }
        if (book.getUpdatedAt() == null) {
            book.setUpdatedAt(now);
        }
        if (book.getLikeCount() == null) {
            book.setLikeCount(0);
        }
        if (book.getAuthor() == null || book.getAuthor().getUserId() == null) {
            throw new IllegalArgumentException("작성자 정보가 없습니다.");
        }

        User realUser = userRepository.findByUserId(book.getAuthor().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        book.setAuthor(realUser);

        if (bookRepository.findIdByTitleAndAuthor(book.getTitle(), realUser).isPresent()) {
            throw new BookAlreadyExistsException(book.getTitle());
        }
        Book savedBook = bookRepository.save(book);
        aiRecommendationService.updateAiRecommendation();
        return savedBook;
    }

    @Transactional
    public Book update(Long id, Book book, String loginUserId) {
        Book existing = findById(id);

        if (existing.getAuthor() == null || !existing.getAuthor().getUserId().equals(loginUserId)) {
            throw new IllegalArgumentException("자신이 등록한 책만 수정할 수 있습니다.");
        }

        User targetAuthor = existing.getAuthor();
        if (book.getAuthor() != null) {
            targetAuthor = userRepository.findByUserId(book.getAuthor().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        }

        if (book.getTitle() != null) {
            Book existsBook = bookRepository.findByTitleAndAuthor(book.getTitle(), targetAuthor).orElse(null);
            if (existsBook != null && !existsBook.getId().equals(id)) {
                throw new BookAlreadyExistsException("이미 작성하신 제목의 도서입니다.");
            }

            existing.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            existing.setAuthor(targetAuthor);
        }
        if (book.getPublisher() != null) {
            existing.setPublisher(book.getPublisher());
        }
        if (book.getContent() != null) {
            existing.setContent(book.getContent());
        }
        if (book.getTags() != null) {
            existing.setTags(book.getTags());
        }
        if (book.getCoverImageUrl() != null) {
            existing.setCoverImageUrl(book.getCoverImageUrl());
        }
        if (book.getLikeCount() != null) {
            existing.setLikeCount(book.getLikeCount());
        }
        if (book.getCreatedAt() != null) {
            existing.setCreatedAt(book.getCreatedAt());
        }
        if (book.getUpdatedAt() != null) {
            existing.setUpdatedAt(book.getUpdatedAt());
        } else {
            existing.setUpdatedAt(now());
        }

        return bookRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, String loginUserId) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        if (book.getAuthor() == null || loginUserId == null ||
                !book.getAuthor().getUserId().trim().equalsIgnoreCase(loginUserId.trim())) {
            throw new IllegalArgumentException("자신이 등록한 책만 삭제할 수 있습니다.");
        }

        long deletedRecommendationCount = aiRecommendationRepository.deleteByRecommendedBook(book);
        likeRepository.deleteByBook(book);
        bookRepository.deleteById(id);

        if (deletedRecommendationCount > 0) {
            aiRecommendationService.updateAiRecommendation();
        }
    }

    @Transactional(readOnly = true)
    public List<Book> search(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    @Transactional
    public Book updateCoverImage(Long id, String coverImageUrl) {
        if (coverImageUrl == null || coverImageUrl.isBlank()) {
            throw new IllegalArgumentException("coverImageUrl is required");
        }

        Book book = findById(id);
        book.setCoverImageUrl(coverImageUrl);
        book.setUpdatedAt(now());
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    public List<Book> searchNew() {
        return bookRepository.findTop3ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Book> searchPopular() {
        return bookRepository.findTop3ByOrderByLikeCountDesc();
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContaining(title);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContaining(author);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByPublisher(String publisher) {
        return bookRepository.findByPublisherContaining(publisher);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByContent(String content) {
        return bookRepository.findByContentContaining(content);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByTags(String tags) {
        return bookRepository.findByTagsContaining(tags);
    }

    @Transactional(readOnly = true)
    public List<Book> searchByKeyword(String keyword) {
        return bookRepository.findByKeyword(keyword);
    }

    @Transactional(readOnly = true)
    public List<Long> findLikedBookIds(String loginUserId) {
        if (loginUserId == null || loginUserId.isBlank()) {
            return List.of();
        }

        return likeRepository.findBookIdsByUserId(loginUserId);
    }

    @Transactional
    public Book like(Long bookId, String userId, String loginUserId) {
        Book book = findById(bookId);

        User user = userRepository.findByUserId(loginUserId)
                .orElseThrow(() -> new RuntimeException("User not found: " + loginUserId));

        Likes existingLike = likeRepository
                .findByUser_UserIdAndBook_Id(loginUserId, bookId)
                .orElse(null);

        Integer currentLikeCount = book.getLikeCount();

        if (currentLikeCount == null) {
            currentLikeCount = 0;
        }

        if (existingLike != null) {
            likeRepository.delete(existingLike);
            book.setLikeCount(Math.max(currentLikeCount - 1, 0));
        } else {
            Likes likes = Likes.builder()
                    .user(user)
                    .book(book)
                    .build();

            likeRepository.save(likes);
            book.setLikeCount(currentLikeCount + 1);
        }

        return bookRepository.save(book);
    }

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
