package dev.dmitriirussu.graph.pagination.jdbc.infrastructure;

import dev.dmitriirussu.graph.pagination.jdbc.application.OwnerReadRepository;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageRequest;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageResult;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerListView;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerView;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of {@link OwnerReadRepository}.
 *
 * <p>Uses {@link OwnerProjectionExtractor} to assemble object graphs
 * from flat SQL result sets, and {@link ViewMapper} to map them to read models.
 */
@Repository
public class JdbcOwnerReadRepository implements OwnerReadRepository {

    private final JdbcClient jdbc;

    JdbcOwnerReadRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    /** SQL queries for owner read operations. */
    private interface Sql {

        String COUNT_ALL = """
        SELECT COUNT(*) FROM owners
        """;

        String SELECT_ALL_PAGED = """
        SELECT o.id          AS owner_id,
               o.name        AS owner_name,
               p.id          AS pet_id,
               p.name        AS pet_name,
               v.id          AS visit_id,
               v.date        AS visit_date
        FROM owners o
        LEFT JOIN pets  p ON p.owner_id = o.id
        LEFT JOIN visits v ON v.pet_id  = p.id
        WHERE o.id IN (
            SELECT id FROM owners ORDER BY id LIMIT :size OFFSET :offset
        )
        ORDER BY o.id
        """;

        String SELECT_ALL_LIST_PAGED = """
        SELECT o.id          AS owner_id,
               o.name        AS owner_name,
               p.name        AS pet_name
        FROM owners o
        LEFT JOIN pets p ON p.owner_id = o.id
        WHERE o.id IN (
            SELECT id FROM owners ORDER BY id LIMIT :size OFFSET :offset
        )
        ORDER BY o.id
        """;
    }

    @Override
    public PageResult<OwnerView> findAllWithGraph(PageRequest pageRequest) {
        long total = countAll();
        List<OwnerView> content = jdbc.sql(Sql.SELECT_ALL_PAGED)
                .param("size",   pageRequest.size())
                .param("offset", (long) pageRequest.page() * pageRequest.size())
                .query(OwnerProjectionExtractor::extractWithGraph)
                .stream()
                .map(ViewMapper::toView)
                .toList();
        return new PageResult<>(content, pageRequest.page(), pageRequest.size(), total);
    }

    // owner + pet names only
    @Override
    public PageResult<OwnerListView> findAllFlat(PageRequest pageRequest) {
        long total = countAll();
        List<OwnerListView> content = jdbc.sql(Sql.SELECT_ALL_LIST_PAGED)
                .param("size",   pageRequest.size())
                .param("offset", (long) pageRequest.page() * pageRequest.size())
                .query(OwnerProjectionExtractor::extractFlat)
                .stream()
                .map(ViewMapper::toListView)
                .toList();
        return new PageResult<>(content, pageRequest.page(), pageRequest.size(), total);
    }

    private long countAll() {
        return jdbc.sql(Sql.COUNT_ALL).query(Long.class).single();
    }
}

/*Подзапрос IN (SELECT id FROM owners ... LIMIT/OFFSET) — ключевой приём:
сначала получаем страницу владельцев, затем тянем их pets/visits.
Без этого LIMIT обрезал бы строки JOIN-а, а не владельцев.
*/