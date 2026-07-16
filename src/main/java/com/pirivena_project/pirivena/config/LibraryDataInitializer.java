package com.pirivena_project.pirivena.config;

import com.pirivena_project.pirivena.modal.Book;
import com.pirivena_project.pirivena.modal.BookCategory;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import com.pirivena_project.pirivena.repository.BookRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LibraryDataInitializer implements ApplicationRunner {

    private final BookCategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    private static final Map<String, String> CATEGORIES = new LinkedHashMap<>();

    static {
        CATEGORIES.put("Tipitaka and Canonical Texts", "The Pali Canon and translations of its principal collections.");
        CATEGORIES.put("Buddhist Philosophy", "Buddhist doctrine, ethics, meditation, and Abhidhamma studies.");
        CATEGORIES.put("Pali Language", "Pali grammar, readers, dictionaries, and language studies.");
        CATEGORIES.put("Buddhist History", "The history of Buddhism, Sri Lankan Buddhism, and monastic traditions.");
        CATEGORIES.put("Sinhala Language and Literature", "Sinhala language, literature, and cultural works.");
        CATEGORIES.put("English and Reference", "English language learning, dictionaries, and general reference works.");
        CATEGORIES.put("General Education", "History, geography, science, mathematics, and other educational subjects.");
    }

    private static final List<BookSeed> BOOKS = List.of(
            new BookSeed("Dhammapada", "Traditional (Khuddaka Nikaya)", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 8),
            new BookSeed("Sutta Nipata", "Traditional (Khuddaka Nikaya)", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 5),
            new BookSeed("Digha Nikaya", "Traditional", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 4),
            new BookSeed("Majjhima Nikaya", "Traditional", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 4),
            new BookSeed("Samyutta Nikaya", "Traditional", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 4),
            new BookSeed("Anguttara Nikaya", "Traditional", "Buddhist Publication Society", "Tipitaka and Canonical Texts", 4),
            new BookSeed("The Questions of King Milinda", "Translated by T. W. Rhys Davids", "Motilal Banarsidass", "Tipitaka and Canonical Texts", 3),

            new BookSeed("Visuddhimagga: The Path of Purification", "Bhadantacariya Buddhaghosa", "Buddhist Publication Society", "Buddhist Philosophy", 5),
            new BookSeed("A Comprehensive Manual of Abhidhamma", "Bhikkhu Bodhi", "Buddhist Publication Society", "Buddhist Philosophy", 5),
            new BookSeed("Abhidhammattha Sangaha", "Acariya Anuruddha", "Buddhist Publication Society", "Buddhist Philosophy", 4),
            new BookSeed("What the Buddha Taught", "Walpola Rahula", "Grove Press", "Buddhist Philosophy", 6),
            new BookSeed("The Buddha and His Teachings", "Narada Maha Thera", "Buddhist Missionary Society", "Buddhist Philosophy", 6),
            new BookSeed("The Noble Eightfold Path", "Bhikkhu Bodhi", "Buddhist Publication Society", "Buddhist Philosophy", 5),
            new BookSeed("Mindfulness in Plain English", "Bhante Henepola Gunaratana", "Wisdom Publications", "Buddhist Philosophy", 4),

            new BookSeed("Introduction to Pali", "A. K. Warder", "Pali Text Society", "Pali Language", 6),
            new BookSeed("A New Course in Reading Pali", "James Gair and W. S. Karunatillake", "Motilal Banarsidass", "Pali Language", 5),
            new BookSeed("Pali Grammar", "Wilhelm Geiger", "Pali Text Society", "Pali Language", 4),
            new BookSeed("The Pali Text Society's Pali-English Dictionary", "T. W. Rhys Davids and William Stede", "Pali Text Society", "Pali Language", 3),

            new BookSeed("Mahavamsa: The Great Chronicle of Ceylon", "Mahanama Thera", "Asian Educational Services", "Buddhist History", 5),
            new BookSeed("History of Buddhism in Ceylon", "Walpola Rahula", "M. D. Gunasena", "Buddhist History", 4),
            new BookSeed("The Life of the Buddha", "Bhikkhu Nanamoli", "Buddhist Publication Society", "Buddhist History", 4),
            new BookSeed("Buddhism in Sri Lanka", "H. R. Perera", "Buddhist Publication Society", "Buddhist History", 4),

            new BookSeed("Madol Doova", "Martin Wickramasinghe", "Sarasa", "Sinhala Language and Literature", 5),
            new BookSeed("Gamperaliya", "Martin Wickramasinghe", "Sarasa", "Sinhala Language and Literature", 4),
            new BookSeed("Viragaya", "Martin Wickramasinghe", "Sarasa", "Sinhala Language and Literature", 4),
            new BookSeed("The Village in the Jungle", "Leonard Woolf", "Oxford University Press", "English and Reference", 4),
            new BookSeed("Oxford Advanced Learner's Dictionary", "Oxford University Press", "Oxford University Press", "English and Reference", 3),
            new BookSeed("A Brief History of Time", "Stephen Hawking", "Bantam Books", "General Education", 3)
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Map<String, BookCategory> categories = new LinkedHashMap<>();
        CATEGORIES.forEach((name, description) -> categories.put(name,
                categoryRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> categoryRepository.save(new BookCategory(null, name, description)))));

        BOOKS.forEach(seed -> {
            if (!bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(seed.title(), seed.author())) {
                Book book = new Book();
                book.setTitle(seed.title());
                book.setAuthor(seed.author());
                book.setPublisher(seed.publisher());
                book.setTotalCopies(seed.copies());
                book.setAvailableCopies(seed.copies());
                book.setBookCategory(categories.get(seed.category()));
                bookRepository.save(book);
            }
        });
    }

    private record BookSeed(String title, String author, String publisher, String category, int copies) { }
}
