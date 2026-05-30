package dev.dmitriirussu.graph.pagination.jdbc.application.pagination;

/**
 * Pagination request model.
 *
 * @param page zero-based page index
 * @param size number of items per page
 */
public record PageRequest(int page, int size) {}
