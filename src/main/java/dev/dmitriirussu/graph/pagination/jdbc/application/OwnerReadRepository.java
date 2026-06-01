package dev.dmitriirussu.graph.pagination.jdbc.application;

import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageQuery;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageResult;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerListView;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerView;

/**
 * Read-only repository for querying owner data.
 *
 * <p>Defined in the application layer as a port —
 * infrastructure provides the implementation.
 */
public interface OwnerReadRepository {
    PageResult<OwnerListView> findAllFlat(PageQuery pageRequest);
    PageResult<OwnerView> findAllWithGraph(PageQuery pageRequest);
}
