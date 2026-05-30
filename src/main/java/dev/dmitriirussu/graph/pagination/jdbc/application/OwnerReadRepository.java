package dev.dmitriirussu.graph.pagination.jdbc.application;

import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageRequest;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageResult;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerListView;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerView;

import java.util.List;
import java.util.Optional;

/**
 * Read-only repository for querying owner data.
 *
 * <p>Defined in the application layer as a port —
 * infrastructure provides the implementation.
 */
public interface OwnerReadRepository {
    PageResult<OwnerListView> findAllFlat(PageRequest pageRequest);
    PageResult<OwnerView> findAllWithGraph(PageRequest pageRequest);
}
