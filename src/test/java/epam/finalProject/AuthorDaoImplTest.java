package epam.finalProject;

import epam.finalProject.DAO.AuthorDaoImpl;
import epam.finalProject.entity.Author;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuthorDaoImplTest {

    private static DataSource ds;
    private AuthorDaoImpl dao;

    @BeforeAll
    static void initDatabase() throws SQLException {
        // 1) настраиваем H2
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        // 2) создаём таблицу authors
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE authors (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  name VARCHAR(255) UNIQUE NOT NULL
                )
                """);
        }
    }

    @BeforeEach
    void setUp() {
        // Инжектим в DAO нашу H2-конфигурацию
        dao = new AuthorDaoImpl(ds);
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE authors");
        }
    }

    @Test
    void saveNewAuthor_shouldReturnTrueAndSetId() {
        Author author = new Author();
        author.setName("Pushkin");

        boolean created = dao.save(author);
        assertTrue(created, "Первый раз — автор должен сохраниться");
        assertNotNull(author.getId(), "ID после вставки не должен быть null");

        // Проверим, что findById действительно возвращает того же автора
        Author fromDb = dao.findById(author.getId());
        assertEquals("Pushkin", fromDb.getName());
    }

    @Test
    void saveExistingAuthor_shouldReturnFalseAndKeepId() throws SQLException {
        // вставим напрямую
        long fixedId;
        try (Connection conn = ds.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO authors (name) VALUES (?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, "Tolstoy");
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            fixedId = rs.getLong(1);
        }

        Author author = new Author();
        author.setName("Tolstoy");
        boolean created = dao.save(author);

        assertFalse(created, "Второй раз — не должно вставляться");
        assertEquals(fixedId, author.getId(), "ID должен подтянуться из БД");
    }

    @Test
    void update_existingAuthor_shouldReturnTrue() {
        // вставим
        Author a = new Author();
        a.setName("Dostoevsky");
        dao.save(a);

        a.setName("Fiodor");
        boolean updated = dao.update(a);
        assertTrue(updated);

        Author fromDb = dao.findById(a.getId());
        assertEquals("Fiodor", fromDb.getName());
    }

    @Test
    void delete_existingAuthor_shouldReturnTrue() {
        Author a = new Author();
        a.setName("Gogol");
        dao.save(a);

        boolean deleted = dao.delete(a.getId());
        assertTrue(deleted);
        assertFalse(dao.existsById(a.getId()));
    }

    @Test
    void findAll_and_existsById() {
        Author a1 = new Author(); a1.setName("A1"); dao.save(a1);
        Author a2 = new Author(); a2.setName("A2"); dao.save(a2);

        List<Author> list = dao.findAll();
        assertEquals(2, list.size());

        assertTrue(dao.existsById(a1.getId()));
        assertTrue(dao.existsById(a2.getId()));
        assertFalse(dao.existsById(999L));
    }

    @Test
    void findById_nonExisting_shouldReturnNull() {
        assertNull(dao.findById(12345L));
    }
}
