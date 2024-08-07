package com.minkuh.prticl.common.wrappers;

import java.util.List;

public class PaginatedResult<T> {
    private List<T> list;
    private int page;
    private int totalPages;

    public PaginatedResult(List<T> list, int page, int totalPages) {
        this.list = list;
        this.page = page;
        this.totalPages = totalPages;
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
}