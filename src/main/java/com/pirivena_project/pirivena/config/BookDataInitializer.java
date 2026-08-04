package com.pirivena_project.pirivena.config;

import com.pirivena_project.pirivena.modal.Book;
import com.pirivena_project.pirivena.modal.BookCategory;
import com.pirivena_project.pirivena.repository.BookCategoryRepository;
import com.pirivena_project.pirivena.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Order(20)
@RequiredArgsConstructor
public class BookDataInitializer implements ApplicationRunner {

    private final BookRepository bookRepository;
    private final BookCategoryRepository categoryRepository;

    private static final Map<String, Integer> CATEGORY_SEQUENCE_START = Map.of(
            "Fiction", 0,
            "Non-Fiction", 10,
            "Literature", 20,
            "Science Fiction", 30,
            "Religion and Buddhism", 40,
            "History", 50,
            "Biography", 60,
            "Education", 70,
            "Children's Books", 80,
            "Reference", 90);

    private static final Map<String, List<BookDefinition>> BOOKS_BY_CATEGORY = Map.of(
            "Fiction", List.of(
                    book("Pride and Prejudice", "Jane Austen", "Penguin Classics"),
                    book("To Kill a Mockingbird", "Harper Lee", "Harper Perennial"),
                    book("The Great Gatsby", "F. Scott Fitzgerald", "Scribner"),
                    book("Jane Eyre", "Charlotte Bronte", "Oxford University Press"),
                    book("Little Women", "Louisa May Alcott", "Penguin Classics"),
                    book("The Kite Runner", "Khaled Hosseini", "Riverhead Books"),
                    book("The Book Thief", "Markus Zusak", "Knopf"),
                    book("The Alchemist", "Paulo Coelho", "HarperOne"),
                    book("Animal Farm", "George Orwell", "Penguin Books"),
                    book("Lord of the Flies", "William Golding", "Faber and Faber")),
            "Non-Fiction", List.of(
                    book("Sapiens", "Yuval Noah Harari", "Harper"),
                    book("Educated", "Tara Westover", "Random House"),
                    book("Silent Spring", "Rachel Carson", "Mariner Books"),
                    book("A Brief History of Time", "Stephen Hawking", "Bantam"),
                    book("Thinking, Fast and Slow", "Daniel Kahneman", "Farrar, Straus and Giroux"),
                    book("The Immortal Life of Henrietta Lacks", "Rebecca Skloot", "Crown"),
                    book("Freakonomics", "Steven D. Levitt and Stephen J. Dubner", "William Morrow"),
                    book("Guns, Germs, and Steel", "Jared Diamond", "W. W. Norton"),
                    book("Into the Wild", "Jon Krakauer", "Anchor Books"),
                    book("Cosmos", "Carl Sagan", "Ballantine Books")),
            "Literature", List.of(
                    book("The Odyssey", "Homer", "Penguin Classics"),
                    book("Hamlet", "William Shakespeare", "Oxford University Press"),
                    book("The Divine Comedy", "Dante Alighieri", "Penguin Classics"),
                    book("One Hundred Years of Solitude", "Gabriel Garcia Marquez", "Harper Perennial"),
                    book("Things Fall Apart", "Chinua Achebe", "Heinemann"),
                    book("Beloved", "Toni Morrison", "Vintage"),
                    book("Moby-Dick", "Herman Melville", "Penguin Classics"),
                    book("Crime and Punishment", "Fyodor Dostoevsky", "Vintage Classics"),
                    book("A Tale of Two Cities", "Charles Dickens", "Penguin Classics"),
                    book("The Metamorphosis", "Franz Kafka", "Schocken Books")),
            "Science Fiction", List.of(
                    book("Dune", "Frank Herbert", "Ace Books"),
                    book("Nineteen Eighty-Four", "George Orwell", "Penguin Books"),
                    book("Foundation", "Isaac Asimov", "Bantam Spectra"),
                    book("The Left Hand of Darkness", "Ursula K. Le Guin", "Ace Books"),
                    book("Neuromancer", "William Gibson", "Ace Books"),
                    book("The Martian", "Andy Weir", "Crown"),
                    book("Fahrenheit 451", "Ray Bradbury", "Simon and Schuster"),
                    book("Ender's Game", "Orson Scott Card", "Tor Books"),
                    book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "Pan Books"),
                    book("The Time Machine", "H. G. Wells", "Penguin Classics")),
            "Religion and Buddhism", List.of(
                    book("What the Buddha Taught", "Walpola Rahula", "Grove Press"),
                    book("The Dhammapada", "Eknath Easwaran", "Nilgiri Press"),
                    book("The Heart of the Buddha's Teaching", "Thich Nhat Hanh", "Harmony"),
                    book("In the Buddha's Words", "Bhikkhu Bodhi", "Wisdom Publications"),
                    book("Old Path White Clouds", "Thich Nhat Hanh", "Parallax Press"),
                    book("Mindfulness in Plain English", "Bhante Henepola Gunaratana", "Wisdom Publications"),
                    book("The World's Religions", "Huston Smith", "HarperOne"),
                    book("The Bhagavad Gita", "Eknath Easwaran", "Nilgiri Press"),
                    book("Tao Te Ching", "Lao Tzu", "Penguin Classics"),
                    book("A History of Religious Ideas", "Mircea Eliade", "University of Chicago Press")),
            "History", List.of(
                    book("The Silk Roads", "Peter Frankopan", "Bloomsbury"),
                    book("SPQR", "Mary Beard", "Liveright"),
                    book("Postwar", "Tony Judt", "Penguin Books"),
                    book("India After Gandhi", "Ramachandra Guha", "HarperCollins"),
                    book("Sri Lanka in the Modern Age", "Nira Wickramasinghe", "Oxford University Press"),
                    book("The Guns of August", "Barbara W. Tuchman", "Random House"),
                    book("The Rise and Fall of the Third Reich", "William L. Shirer", "Simon and Schuster"),
                    book("A People's History of the United States", "Howard Zinn", "Harper Perennial"),
                    book("The History of the Ancient World", "Susan Wise Bauer", "W. W. Norton"),
                    book("The Penguin History of the World", "J. M. Roberts", "Penguin Books")),
            "Biography", List.of(
                    book("Long Walk to Freedom", "Nelson Mandela", "Little, Brown"),
                    book("The Diary of a Young Girl", "Anne Frank", "Penguin Books"),
                    book("Steve Jobs", "Walter Isaacson", "Simon and Schuster"),
                    book("Becoming", "Michelle Obama", "Crown"),
                    book("Wings of Fire", "A. P. J. Abdul Kalam", "Universities Press"),
                    book("Einstein: His Life and Universe", "Walter Isaacson", "Simon and Schuster"),
                    book("Autobiography of a Yogi", "Paramahansa Yogananda", "Self-Realization Fellowship"),
                    book("The Story of My Experiments with Truth", "Mahatma Gandhi", "Beacon Press"),
                    book("I Am Malala", "Malala Yousafzai", "Little, Brown"),
                    book("Leonardo da Vinci", "Walter Isaacson", "Simon and Schuster")),
            "Education", List.of(
                    book("Pedagogy of the Oppressed", "Paulo Freire", "Bloomsbury Academic"),
                    book("Visible Learning", "John Hattie", "Routledge"),
                    book("Make It Stick", "Peter C. Brown", "Harvard University Press"),
                    book("How Children Succeed", "Paul Tough", "Mariner Books"),
                    book("Mindstorms", "Seymour Papert", "Basic Books"),
                    book("Teaching to Transgress", "bell hooks", "Routledge"),
                    book("The Courage to Teach", "Parker J. Palmer", "Jossey-Bass"),
                    book("Why Don't Students Like School?", "Daniel T. Willingham", "Jossey-Bass"),
                    book("Understanding by Design", "Grant Wiggins and Jay McTighe", "ASCD"),
                    book("The First Days of School", "Harry K. Wong", "Harry K. Wong Publications")),
            "Children's Books", List.of(
                    book("Charlotte's Web", "E. B. White", "HarperCollins"),
                    book("Matilda", "Roald Dahl", "Puffin Books"),
                    book("The Hobbit", "J. R. R. Tolkien", "HarperCollins"),
                    book("The Secret Garden", "Frances Hodgson Burnett", "Puffin Classics"),
                    book("Alice's Adventures in Wonderland", "Lewis Carroll", "Macmillan"),
                    book("The Wind in the Willows", "Kenneth Grahame", "Puffin Classics"),
                    book("Charlie and the Chocolate Factory", "Roald Dahl", "Puffin Books"),
                    book("Anne of Green Gables", "L. M. Montgomery", "Puffin Classics"),
                    book("The Little Prince", "Antoine de Saint-Exupery", "Wordsworth Editions"),
                    book("The Jungle Book", "Rudyard Kipling", "Puffin Classics")),
            "Reference", List.of(
                    book("Oxford Advanced Learner's Dictionary", "Oxford University Press", "Oxford University Press"),
                    book("Encyclopaedia Britannica Concise", "Encyclopaedia Britannica", "Britannica"),
                    book("National Geographic Atlas of the World", "National Geographic", "National Geographic"),
                    book("Roget's International Thesaurus", "Barbara Ann Kipfer", "Collins Reference"),
                    book("The Chicago Manual of Style", "University of Chicago Press", "University of Chicago Press"),
                    book("The Elements of Style", "William Strunk Jr. and E. B. White", "Pearson"),
                    book("Guinness World Records", "Guinness World Records", "Guinness World Records"),
                    book("DK Children's Encyclopedia", "DK", "DK Publishing"),
                    book("Merriam-Webster's Collegiate Dictionary", "Merriam-Webster", "Merriam-Webster"),
                    book("The National Atlas of Sri Lanka", "Survey Department of Sri Lanka", "Survey Department")));

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Map.Entry<String, List<BookDefinition>> categoryEntry : BOOKS_BY_CATEGORY.entrySet()) {
            int sequence = CATEGORY_SEQUENCE_START.get(categoryEntry.getKey()) + 1;
            BookCategory category = categoryRepository
                    .findByNameIgnoreCase(categoryEntry.getKey())
                    .orElseThrow(() -> new IllegalStateException(
                            "Required book category is missing: " + categoryEntry.getKey()));

            for (BookDefinition definition : categoryEntry.getValue()) {
                if (!bookRepository.existsByTitleIgnoreCaseAndAuthorIgnoreCase(
                        definition.title(), definition.author())) {
                    int copies = 2 + (sequence % 4);
                    bookRepository.save(new Book(
                            null,
                            definition.title(),
                            definition.author(),
                            createIsbn13(sequence),
                            definition.publisher(),
                            copies,
                            copies,
                            category));
                }
                sequence++;
            }
        }
    }

    private static BookDefinition book(String title, String author, String publisher) {
        return new BookDefinition(title, author, publisher);
    }

    private static String createIsbn13(int sequence) {
        String firstTwelveDigits = "978955900" + String.format("%03d", sequence);
        int weightedSum = 0;
        for (int index = 0; index < firstTwelveDigits.length(); index++) {
            int digit = firstTwelveDigits.charAt(index) - '0';
            weightedSum += digit * (index % 2 == 0 ? 1 : 3);
        }
        int checkDigit = (10 - weightedSum % 10) % 10;
        return firstTwelveDigits + checkDigit;
    }

    private record BookDefinition(String title, String author, String publisher) {
    }
}
