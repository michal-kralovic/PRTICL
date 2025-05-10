package com.minkuh.prticl.common;

import com.minkuh.prticl.data.entities.IPrticlEntity;

import java.util.List;

public class PaginatedResult<T extends IPrticlEntity> {
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final List<T> items;
    private final int totalItems;
    private final int page;
    private final int pageSize;
    private final int totalPages;

    public PaginatedResult(List<T> items, int totalItems, int page, int pageSize) {
        this.items = items;
        this.totalItems = totalItems;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    public List<T> getItems() {
        return items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasNextPage() {
        return page < totalPages - 1;
    }

    public boolean hasPreviousPage() {
        return page > 0;
    }
}