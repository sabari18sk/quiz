package com.quizplatform.dto;

import java.util.List;

public class QuestionListResponse {

    private List<QuestionDto> questions;
    private long total;
    private int page;
    private int size;
    private int totalPages;
    private boolean last;

    public QuestionListResponse() {}

    public QuestionListResponse(List<QuestionDto> questions, long total, int page, int size, int totalPages, boolean last) {
        this.questions = questions;
        this.total = total;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.last = last;
    }

    public List<QuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDto> questions) {
        this.questions = questions;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
