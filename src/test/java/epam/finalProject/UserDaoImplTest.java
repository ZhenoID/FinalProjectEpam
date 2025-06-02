package epam.finalProject;

import epam.finalProject.DAO.UserDaoImpl;
import epam.finalProject.entity.User;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoImplTest {

    private DataSource ds;
    private UserDaoImpl dao;

    @BeforeAll
    void initDatabase() throws Exception {
        // 1) Настраиваем H2 in-memory
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2.setUser("sa");
        h2.setPassword("");
        ds = h2;

        // 2) Создаём таблицу users
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE users (
                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                  username VARCHAR(255) UNIQUE NOT NULL,
                  password VARCHAR(255) NOT NULL,
                  role VARCHAR(50) NOT NULL
                );
            """);
        }
    }

    @BeforeEach
    void setUp() {
        // Инжектим H2-DataSource в DAO
        dao = new UserDaoImpl(ds);
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE users");
        }
    }

    @Test
    void save_and_findByUsername() {
        User u = new User();
        u.setUsername("alice");
        u.setPassword("secret");
        u.setRole("ROLE_ADMIN");

        assertTrue(dao.save(u), "save() должен вернуть true для нового пользователя");

        User fromDb = dao.findByUsername("alice");
        assertNotNull(fromDb, "findByUsername должен вернуть объект");
        assertEquals("alice", fromDb.getUsername());
        assertEquals("secret", fromDb.getPassword());
        assertEquals("ROLE_ADMIN", fromDb.getRole());
    }

    @Test
    void findByUsername_nonExisting_returnsNull() {
        assertNull(dao.findByUsername("no_such_user"));
    }

    @Test
    void findAll_and_delete() {
        // вставляем двух пользователей
        User u1 = new User(); u1.setUsername("u1"); u1.setPassword("p1"); u1.setRole("ROLE_USER");
        User u2 = new User(); u2.setUsername("u2"); u2.setPassword("p2"); u2.setRole("ROLE_USER");
        assertTrue(dao.save(u1));
        assertTrue(dao.save(u2));

        List<User> list = dao.findAll();
        assertEquals(2, list.size(), "findAll должен вернуть 2 записи");

        // удаляем первого
        User toDelete = dao.findByUsername("u1");
        assertTrue(dao.delete(toDelete), "delete() должен вернуть true");
        assertNull(dao.findByUsername("u1"), "после удаления findByUsername вернёт null");
    }

    @Test
    void updatePassword_and_findById() {
        User u = new User();
        u.setUsername("bob");
        u.setPassword("old");
        u.setRole("ROLE_USER");
        assertTrue(dao.save(u));

        // меняем пароль
        u.setPassword("newpass");
        assertTrue(dao.update(u), "update() должен вернуть true");

        // берём ID через findAll (или findByUsername + доп. запрос findAll)
        List<User> all = dao.findAll();
        assertFalse(all.isEmpty());
        Long id = all.get(0).getId();

        User byId = dao.findById(id);
        assertNotNull(byId, "findById должен вернуть пользователя");
        assertEquals("newpass", byId.getPassword());
        assertEquals("bob", byId.getUsername());
    }
}
