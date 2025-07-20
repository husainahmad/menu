package com.harmoni.pos.http.utils;

import com.github.pagehelper.PageHelper;

/**
 * Utility class for applying pagination to queries.
 */
public class PaginationUtils {

    /**
     * Private constructor to prevent instantiation.
     */
    private PaginationUtils() {
        throw new IllegalArgumentException("Illegal exception");
    }

    /**
     * Applies pagination to the current query using PageHelper.
     *
     * @param startPage the starting page number (minimum 1)
     * @param sizePage  the size of each page (minimum 1)
     */
    public static void applyPagination(int startPage, int sizePage) {
        int page = Math.max(startPage, 1);
        int size = Math.max(sizePage, 1);
        PageHelper.startPage(page, size, true);
    }
}
