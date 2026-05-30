package dev.dmitriirussu.graph.pagination.jdbc.application.view;

import java.time.LocalDate;

/**
 * Application read model.
 */
public record VisitView (String id, LocalDate date, String petId) {}
