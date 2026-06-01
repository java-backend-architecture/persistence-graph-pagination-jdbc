package dev.dmitriirussu.graph.pagination.jdbc.infrastructure;

import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageQuery;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageResult;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerListView;
import dev.dmitriirussu.graph.pagination.jdbc.application.view.OwnerView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.JdbcTest;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
class JdbcOwnerReadRepositoryTest {

    @Autowired
    JdbcClient jdbc;

    JdbcOwnerReadRepository repository;

    @BeforeEach
    void setUp() {
        repository = new JdbcOwnerReadRepository(jdbc);
    }

    // ── findAllWithGraph ───────────────────────────────────────

    @Test
    void findAllWithGraph_returnsRequestedPage() {
        PageResult<OwnerView> result = repository.findAllWithGraph(new PageQuery(0, 1));

        assertThat(result.content()).hasSize(1);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.total()).isEqualTo(3);
    }

    @Test
    void findAllWithGraph_returnsOwnerWithPetsAndVisits() {
        PageResult<OwnerView> result = repository.findAllWithGraph(new PageQuery(0, 1));

        OwnerView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("jack");
        assertThat(owner.pets()).hasSize(2);
        assertThat(owner.pets().get(0).visits()).hasSize(2);
    }

    @Test
    void findAllWithGraph_returnsOwnerWithNoPets_onLastPage() {
        PageResult<OwnerView> result = repository.findAllWithGraph(new PageQuery(1, 1));

        OwnerView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("ann");
        assertThat(owner.pets()).hasSize(1);
    }

    @Test
    void findAllWithGraph_returnsEmptyContent_whenPageBeyondTotal() {
        PageResult<OwnerView> result = repository.findAllWithGraph(new PageQuery(99, 10));

        assertThat(result.content()).isEmpty();
        assertThat(result.total()).isEqualTo(3);
    }

    @Test
    void findAllWithGraph_graphIsComplete_notTruncatedByJoin() {
        // owner 'bob' has 4 pets — verifies subquery pagination doesn't truncate JOIN rows
        PageResult<OwnerView> result = repository.findAllWithGraph(new PageQuery(2, 1));

        OwnerView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("bob");
        assertThat(owner.pets()).hasSize(4);
    }

    // ── findAllFlat ────────────────────────────────────────────

    @Test
    void findAllFlat_returnsRequestedPage() {
        PageResult<OwnerListView> result = repository.findAllFlat(new PageQuery(0, 2));

        assertThat(result.content()).hasSize(2);
        assertThat(result.page()).isEqualTo(0);
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.total()).isEqualTo(3);
    }

    @Test
    void findAllFlat_returnsPetNamesForOwner() {
        PageResult<OwnerListView> result = repository.findAllFlat(new PageQuery(0, 1));

        OwnerListView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("jack");
        assertThat(owner.pets()).containsExactly("buddy1", "buddy2");
    }

    @Test
    void findAllFlat_returnsOwnerWithNoPets_asEmptyList() {
        // ann has 1 pet — milo
        PageResult<OwnerListView> result = repository.findAllFlat(new PageQuery(1, 1));

        OwnerListView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("ann");
        assertThat(owner.pets()).containsExactly("milo");
    }

    @Test
    void findAllFlat_returnsEmptyContent_whenPageBeyondTotal() {
        PageResult<OwnerListView> result = repository.findAllFlat(new PageQuery(99, 10));

        assertThat(result.content()).isEmpty();
        assertThat(result.total()).isEqualTo(3);
    }

    @Test
    void findAllFlat_petListIsComplete_notTruncatedByJoin() {
        // bob has 4 pets — verifies subquery pagination doesn't truncate JOIN rows
        PageResult<OwnerListView> result = repository.findAllFlat(new PageQuery(2, 1));

        OwnerListView owner = result.content().get(0);
        assertThat(owner.name()).isEqualTo("bob");
        assertThat(owner.pets()).containsExactly("hew1", "hew2", "hew3", "hew4");
    }
}

