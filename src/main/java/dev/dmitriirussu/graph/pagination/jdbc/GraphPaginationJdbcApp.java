package dev.dmitriirussu.graph.pagination.jdbc;

import dev.dmitriirussu.graph.pagination.jdbc.application.OwnerReadRepository;
import dev.dmitriirussu.graph.pagination.jdbc.application.pagination.PageRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GraphPaginationJdbcApp {
	public static void main(String[] args) {
		SpringApplication.run(GraphPaginationJdbcApp.class, args);
	}
	// Demo output for manual verification of graph extraction queries
	@Bean
	CommandLineRunner demo(OwnerReadRepository repository) {
		return args -> {
			System.out.println("\n=== All owners - full graph ===\n");

			System.out.println(repository.findAllWithGraph(new PageRequest(0, 1)));

			System.out.println();
			System.out.println("\n=== All owners - flat projection ===\n");

			System.out.println(repository.findAllFlat(new PageRequest(0, 1)));
			System.out.println();
		};
	}
}
