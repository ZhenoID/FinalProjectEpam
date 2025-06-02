package epam.finalProject;

import epam.finalProject.DAO.BookDao;
import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;
import epam.finalProject.service.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookDao bookDao;

    @InjectMocks
    private BookServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteBook_ShouldReturnTrue_WhenDaoReturnsTrue() {
        Book book = new Book();
        when(bookDao.deleteBook(book)).thenReturn(true);

        boolean result = service.deleteBook(book);
        assertTrue(result);
        verify(bookDao).deleteBook(book);
    }

    @Test
    void changeBook_ShouldReturnFalse_WhenDaoReturnsFalse() {
        Book book = new Book();
        when(bookDao.changeBook(book)).thenReturn(false);

        boolean result = service.changeBook(book);
        assertFalse(result);
        verify(bookDao).changeBook(book);
    }

    @Test
    void findAll_ShouldReturnList_FromDao() {
        var b1 = new Book(); var b2 = new Book();
        List<Book> list = List.of(b1, b2);
        when(bookDao.findAll()).thenReturn(list);

        List<Book> result = service.findAll();
        assertSame(list, result);
        assertEquals(2, result.size());
        verify(bookDao).findAll();
    }

    @Test
    void findById_ShouldReturnBook_FromDao() {
        Book expected = new Book();
        expected.setId(7L);
        when(bookDao.findById(7L)).thenReturn(expected);

        Book actual = service.findById(7L);
        assertSame(expected, actual);
        verify(bookDao).findById(7L);
    }

    @Test
    void saveBookWithAuthor_ShouldDelegateToDao() {
        Book book = new Book();
        Author author = new Author();
        when(bookDao.saveBookWithAuthor(book, author)).thenReturn(true);

        boolean ok = service.saveBookWithAuthor(book, author);
        assertTrue(ok);
        verify(bookDao).saveBookWithAuthor(book, author);
    }
}
