package epam.finalProject.DAO;

import epam.finalProject.db.ConnectionPool;
import epam.finalProject.entity.Book;
import epam.finalProject.entity.Author;
import epam.finalProject.entity.Genre;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    private static final Logger logger = LoggerFactory.getLogger(BookDaoImpl.class);

    private static final String FIND_AUTHOR_SQL =
            "SELECT id FROM authors WHERE name = ?";
    private static final String INSERT_AUTHOR_SQL =
            "INSERT INTO authors (name) VALUES (?)";
    private static final String INSERT_BOOK_SQL =
            "INSERT INTO books (title, year, author_id, description, quantity) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_BOOK_GENRE_SQL =
            "INSERT INTO book_genres (book_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_BOOK_SQL =
            "DELETE FROM books WHERE id = ?";
    private static final String UPDATE_BOOK_SQL =
            "UPDATE books SET title = ?, author_id = ?, year = ?, description = ?, quantity = ? WHERE id = ?";
    private static final String SELECT_ALL_BOOKS_SQL =
            "SELECT * FROM books ORDER BY id";
    private static final String SELECT_BOOK_BY_ID_SQL =
            "SELECT * FROM books WHERE id = ?";
    private static final String SELECT_GENRES_FOR_BOOK_SQL =
            "SELECT g.id, g.name FROM genres g JOIN book_genres bg ON g.id = bg.genre_id WHERE bg.book_id = ?";

    private final DataSource ds;

    // Constructor for tests
    public BookDaoImpl(DataSource ds) {
        this.ds = ds;
    }

    // Production constructor
    public BookDaoImpl() {
        this.ds = null;
    }

    private Connection getConnection() throws SQLException {
        return (ds != null) ? ds.getConnection() : ConnectionPool.getInstance().getConnection();
    }

    @Override
    public boolean deleteBook(Book book) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BOOK_SQL)) {
            ps.setLong(1, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting book", e);
            return false;
        }
    }

    @Override
    public boolean changeBook(Book book) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_BOOK_SQL)) {
            ps.setString(1, book.getTitle());
            ps.setLong(2, book.getAuthorId());
            ps.setInt(3, book.getYear());
            ps.setString(4, book.getDescription());
            ps.setInt(5, book.getQuantity());
            ps.setLong(6, book.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating book", e);
            return false;
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL_BOOKS_SQL);
             ResultSet rs = ps.executeQuery()) {

            AuthorDao authorDao = new AuthorDaoImpl(ds);

            while (rs.next()) {
                Book book = mapBasicBook(rs);

                Author author = authorDao.findById(book.getAuthorId());
                book.setAuthor(author);

                book.setGenres(loadGenres(conn, book.getId()));
                books.add(book);
            }
        } catch (SQLException e) {
            logger.error("Error fetching all books", e);
        }
        return books;
    }

    @Override
    public Book findById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BOOK_BY_ID_SQL)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Book book = mapBasicBook(rs);

                    Author author = new AuthorDaoImpl(ds).findById(book.getAuthorId());
                    book.setAuthor(author);

                    book.setGenres(loadGenres(conn, id));
                    return book;
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching book by id", e);
        }
        return null;
    }

    @Override
    public boolean saveBookWithAuthor(Book book, Author author) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            long authorId = getOrCreateAuthor(conn, author);
            long bookId = insertBook(conn, book, authorId);
            book.setId(bookId);
            insertBookGenres(conn, bookId, book.getGenreIds());
            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Error saving book with author", e);
            return false;
        }
    }

    private long getOrCreateAuthor(Connection conn, Author author) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(FIND_AUTHOR_SQL)) {
            ps.setString(1, author.getName());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id");
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(INSERT_AUTHOR_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Unable to get author ID");
    }

    private long insertBook(Connection conn, Book book, long authorId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(INSERT_BOOK_SQL, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setInt(2, book.getYear());
            ps.setLong(3, authorId);
            ps.setString(4, book.getDescription());
            ps.setInt(5, book.getQuantity());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Unable to get book ID");
    }

    private void insertBookGenres(Connection conn, long bookId, List<Long> genreIds) throws SQLException {
        if (genreIds == null || genreIds.isEmpty()) return;
        try (PreparedStatement ps = conn.prepareStatement(INSERT_BOOK_GENRE_SQL)) {
            for (Long gid : genreIds) {
                ps.setLong(1, bookId);
                ps.setLong(2, gid);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private List<Genre> loadGenres(Connection conn, long bookId) throws SQLException {
        List<Genre> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT_GENRES_FOR_BOOK_SQL)) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Genre g = new Genre();
                    g.setId(rs.getLong("id"));
                    g.setName(rs.getString("name"));
                    list.add(g);
                }
            }
        }
        return list;
    }

    private Book mapBasicBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthorId(rs.getLong("author_id"));
        book.setYear(rs.getInt("year"));
        book.setDescription(rs.getString("description"));
        book.setQuantity(rs.getInt("quantity"));
        return book;
    }

    @Override
    public boolean decrementQuantity(Long bookId, int amount) {
        String sql = "UPDATE books SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setLong(2, bookId);
            ps.setInt(3, amount);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error decrementing quantity", e);
            return false;
        }
    }

}
