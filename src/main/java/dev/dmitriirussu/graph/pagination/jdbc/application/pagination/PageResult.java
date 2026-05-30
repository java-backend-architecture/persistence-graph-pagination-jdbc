package dev.dmitriirussu.graph.pagination.jdbc.application.pagination;

import java.util.List;
import java.util.function.Function;

/**
 * Generic page result wrapper.
 *
 * @param <T> type of content in page
 */
public record PageResult<T>(List<T> content, int page, int size, long total) {}
