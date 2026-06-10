package com.aivle.bookapp.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BookListResponse {
    private List<BookResponse> content;
    private int totalPages;
    private int currentPage;


}
