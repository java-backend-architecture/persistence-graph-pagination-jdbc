# persistence-graph-pagination-jdbc

Offset-based pagination over a multi-level object graph (owner → pets → visits) using JDBC — no Spring Data, pure SQL, clean architecture.

## What's inside

- `PageRequest` — page index (zero-based) and page size
- `PageResult<T>` — content + pagination metadata, framework-agnostic
- `OwnerReadRepository` — port in the application layer with two query modes
- `JdbcOwnerReadRepository` — JDBC implementation with subquery-based pagination
- `OwnerProjectionExtractor` — assembles a flat `ResultSet` back into an object graph via `computeIfAbsent`
- `OwnerProjection`, `PetProjection`, `VisitProjection`, `ViewMapper` — package-private, never leak outside infrastructure

## Two query modes

| Method | Returns | Graph depth |
|---|---|---|
| `findAllWithGraph` | `PageResult<OwnerView>` | owner → pets → visits |
| `findAllFlat` | `PageResult<OwnerListView>` | owner → pet names only |

## How it works

```java
// full graph — page 0, 1 owner per page
repository.findAllWithGraph(new PageRequest(0, 1));

// flat projection — page 0, 1 owner per page
repository.findAllFlat(new PageRequest(0, 1));
```

### Why a subquery

A JOIN on owner → pets → visits multiplies rows: one owner with 3 pets and 5 visits = 15 rows. A naive `LIMIT` would cut rows, not owners — returning incomplete graphs. The subquery fixes that:

```sql
WHERE o.id IN (
    SELECT id FROM owners ORDER BY id LIMIT :size OFFSET :offset
)
```

First select exactly N owner IDs, then fetch their full graph — no row truncation.

## Architecture

```
application/
  OwnerReadRepository     ← port (interface)
  pagination/
    PageRequest           ← pagination input
    PageResult<T>         ← pagination output
  view/
    OwnerView             ← full graph read model
    OwnerListView         ← flat read model
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

`PageRequest` and `PageResult` have zero framework dependencies — copy them into any Java project.

## Previous

[persistence-flat-pagination-jdbc](https://github.com/java-backend-architecture/persistence-flat-pagination-jdbc) — pagination over a flat entity, no graph.
