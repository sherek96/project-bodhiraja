package com.pirivena_project.pirivena.config;

import com.pirivena_project.pirivena.modal.BookCategory;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Order(10)
@RequiredArgsConstructor
public class BookCategoryDataInitializer implements ApplicationRunner {

    private final BookCategoryRepository categoryRepository;

    private static final List<CategoryDefinition> DEFAULT_CATEGORIES = List.of(
            new CategoryDefinition("Fiction", "Novels and imaginative narrative works."),
            new CategoryDefinition("Non-Fiction", "Factual and informative works based on real subjects."),
            new CategoryDefinition("Literature", "Classical and contemporary literary works."),
            new CategoryDefinition("Science Fiction", "Stories involving futuristic science, technology, space, or alternate worlds."),
            new CategoryDefinition("Religion and Buddhism", "Buddhist teachings, religious studies, scriptures, and spiritual works."),
            new CategoryDefinition("History", "Historical events, societies, cultures, and civilizations."),
            new CategoryDefinition("Biography", "Biographies, autobiographies, and personal memoirs."),
            new CategoryDefinition("Education", "Academic, teaching, learning, and professional-development materials."),
            new CategoryDefinition("Children's Books", "Stories and educational books written for younger readers."),
            new CategoryDefinition("Reference", "Dictionaries, encyclopedias, handbooks, and other reference materials."));

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        DEFAULT_CATEGORIES.stream()
                .filter(category -> !categoryRepository.existsByNameIgnoreCase(category.name()))
                .map(category -> new BookCategory(
                        null, category.name(), category.description()))
                .forEach(categoryRepository::save);
    }

    private record CategoryDefinition(String name, String description) {
    }
}
