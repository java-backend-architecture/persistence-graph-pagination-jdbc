/**
 * JDBC implementation of the read repository.
 *
 * <p>All classes are intentionally kept in one package to leverage
 * package-private visibility as an encapsulation boundary.
 *
 * <p>Public API: {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.JdbcOwnerReadRepository}
 *
 * <p>Internal (package-private by design):
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.OwnerProjection},
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.PetProjection},
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.VisitProjection},
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.OwnerListProjection},
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.OwnerProjectionExtractor},
 * {@link dev.dmitriirussu.graph.pagination.jdbc.infrastructure.ViewMapper}
 */
package dev.dmitriirussu.graph.pagination.jdbc.infrastructure;