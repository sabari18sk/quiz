package com.quizplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultListResponse {
    private List<ResultDto> results;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static ResultListResponse fromPage(Page<ResultDto> page) {
        return ResultListResponse.builder()
                .results(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
