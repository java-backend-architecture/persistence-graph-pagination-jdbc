# persistence-graph-pagination-jdbc

Offset-based pagination over a multi-level object graph (`owner → pets → visits`) using JDBC — no Spring Data, pure SQL, clean architecture.

## What it shows

* Paginating a three-level object graph without breaking graph completeness
* Subquery-based pagination — `LIMIT / OFFSET` applied to owner IDs, not JOIN rows
* Using `package-private` classes as an encapsulation boundary inside the infrastructure layer
* Two query modes:
  * full graph — Owner with Pets and Visits (`OwnerView`)
  * flat list — Owner with pet names only (`OwnerListView`)

## Stack

* Java 25
* Spring Boot
* Spring JDBC (`JdbcClient`)
* H2 (in-memory database)

## Structure

```
application/
    OwnerReadRepository     ← port (interface)
    pagination/
        PageQuery           ← pagination input
        PageResult<T>       ← pagination output
    view/
        OwnerView           ← full graph read model
        OwnerListView       ← flat read model
        PetView
        VisitView

infrastructure/
    JdbcOwnerReadRepository     ← JDBC implementation
    OwnerProjectionExtractor    ← ResultSet → object graph
    OwnerProjection             ← internal
    OwnerListProjection         ← internal
    PetProjection               ← internal
    VisitProjection             ← internal
    ViewMapper                  ← projection → view
```

## Key idea

A `JOIN` on `owner → pets → visits` multiplies rows: one owner with 3 pets and 5 visits = 15 rows. A naive `LIMIT` would cut rows, not owners — returning incomplete graphs. The subquery fixes that:

```sql
WHERE o.id IN (
    SELECT id FROM owners ORDER BY id LIMIT :size OFFSET :offset
)
```

First select exactly N owner IDs, then fetch their full graph — no row truncation.

## Two query modes

| Method | Returns | Graph depth |
|---|---|---|
| `findAllWithGraph` | `PageResult<OwnerView>` | owner → pets → visits |
| `findAllFlat` | `PageResult<OwnerListView>` | owner → pet names only |

## How it works

```java
// full graph — page 0, 1 owner per page
repository.findAllWithGraph(new PageQuery(0, 1));

// flat projection — page 0, 1 owner per page
repository.findAllFlat(new PageQuery(0, 1));
```

`PageQuery` and `PageResult` have zero framework dependencies — copy them into any Java project.

## Tests

Integration tests in `src/test/java` cover pagination correctness, graph completeness, and edge cases — including verification that the subquery prevents JOIN row truncation.

## Related

* [persistence-graph-extraction-jdbc](https://github.com/java-backend-architecture/persistence-graph-extraction-jdbc) — same graph extraction without pagination
* [persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) — pagination over a flat entity, no graph

## Run

```bash
./mvnw spring-boot:run
```
