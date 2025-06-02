package epam.finalProject;

import epam.finalProject.DAO.AuthorDaoImpl;
import epam.finalProject.DAO.BookDaoImpl;
import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookDaoImplTest {

    private static DataSource ds;
    private BookDaoImpl bookDao;
    private AuthorDaoImpl authorDao;

    @BeforeAll
    static void initDatabase() throws Exception {
        // 1) Настраиваем H2 in-memory
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        // 2) Создаём нужные таблицы
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE authors (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  name VARCHAR(255) UNIQUE NOT NULL
                );
                """);

            stmt.execute("""
                CREATE TABLE books (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  title VARCHAR(255) NOT NULL,
                  author_id BIGINT NOT NULL,
                  year INT,
                  description VARCHAR(1000),
                  FOREIGN KEY(author_id) REFERENCES authors(id)
                );
                """);
        }
    }

    @BeforeEach
    void setUp() {
        bookDao = new BookDaoImpl(ds);
        authorDao = new AuthorDaoImpl(ds);
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE books");
            stmt.execute("TRUNCATE TABLE authors");
        }
    }

    @Test
    void saveBookWithAuthor_whenAuthorNew_thenInsertsBoth() {
        Book book = new Book();
        book.setTitle("War and Peace");
        book.setYear(1869);
        book.setDescription("Epic novel");
        Author author = new Author();
        author.setName("Tolstoy");

        boolean ok = bookDao.saveBookWithAuthor(book, author);
        assertTrue(ok, "должен успешно вставить книгу и автора");
        List<Author> authors = authorDao.findAll();
        assertEquals(1, authors.size());
        assertEquals("Tolstoy", authors.get(0).getName());

        List<Book> books = bookDao.findAll();
        assertEquals(1, books.size());
        Book b = books.get(0);
        assertEquals("War and Peace", b.getTitle());
        assertEquals(1869, b.getYear());
        assertEquals("Tolstoy", b.getAuthor().getName());
    }

    @Test
    void saveBookWithAuthor_whenAuthorExists_thenOnlyInsertBook() {
        Author a = new Author(); a.setName("Dostoevsky");
        boolean created = authorDao.save(a);
        assertTrue(created);

        Book book = new Book();
        book.setTitle("Crime and Punishment");
        book.setYear(1866);
        book.setDescription("Psychological novel");
        Author same = new Author(); same.setName("Dostoevsky");

        boolean ok = bookDao.saveBookWithAuthor(book, same);
        assertTrue(ok, "должен вставить книгу, не дублируя автора");

        List<Author> authors = authorDao.findAll();
        assertEquals(1, authors.size());

        List<Book> books = bookDao.findAll();
        assertEquals(1, books.size());
    }

    @Test
    void findById_and_changeBook_and_deleteBook() {
        Book book = new Book();
        book.setTitle("Original");
        book.setYear(2000);
        book.setDescription("Desc");
        Author author = new Author(); author.setName("AuthorX");
        bookDao.saveBookWithAuthor(book, author);

        Book saved = bookDao.findAll().get(0);
        Long id = saved.getId();

        saved.setTitle("Updated");
        saved.setYear(2021);
        saved.setDescription("NewDesc");
        boolean upd = bookDao.changeBook(saved);
        assertTrue(upd, "должен обновиться");

        Book afterUpd = bookDao.findById(id);
        assertEquals("Updated", afterUpd.getTitle());
        assertEquals(2021, afterUpd.getYear());

        boolean del = bookDao.deleteBook(afterUpd);
        assertTrue(del, "должен удалиться");

        assertNull(bookDao.findById(id), "после удаления findById вернёт null");
    }

    @Test
    void findAll_empty_thenReturnsEmptyList() {
        assertTrue(bookDao.findAll().isEmpty());
    }
}
