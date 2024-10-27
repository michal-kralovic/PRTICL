package com.minkuh.prticl.common;

import java.util.List;

public class PaginatedResult<T> {
    private List<T> list;
    private int page;
    private int totalPages;
    private int totalCount;

    public PaginatedResult(List<T> list, int page, int totalPages, int totalCount) {
        this.list = list;
        this.page = page;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}