package epam.finalProject;

import epam.finalProject.DAO.AuthorDao;
import epam.finalProject.entity.Author;
import epam.finalProject.service.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;

    @InjectMocks
    private AuthorServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldReturnTrue_WhenDaoReturnsTrue() {
        Author author = new Author();
        when(authorDao.save(author)).thenReturn(true);

        boolean result = service.save(author);

        assertTrue(result);
        verify(authorDao).save(author);
    }

    @Test
    void update_ShouldReturnFalse_WhenDaoReturnsFalse() {
        Author author = new Author();
        when(authorDao.update(author)).thenReturn(false);

        boolean result = service.update(author);

        assertFalse(result);
        verify(authorDao).update(author);
    }

    @Test
    void delete_ShouldDelegateToDao() {
        Long id = 123L;
        when(authorDao.delete(id)).thenReturn(true);

        boolean result = service.delete(id);

        assertTrue(result);
        verify(authorDao).delete(id);
    }

    @Test
    void findById_ShouldReturnAuthor_FromDao() {
        Author expected = new Author();
        expected.setId(42L);
        when(authorDao.findById(42L)).thenReturn(expected);

        Author actual = service.findById(42L);

        assertSame(expected, actual);
        verify(authorDao).findById(42L);
    }

    @Test
    void findAll_ShouldReturnList_FromDao() {
        List<Author> list = List.of(new Author(), new Author());
        when(authorDao.findAll()).thenReturn(list);

        List<Author> result = service.findAll();

        assertEquals(2, result.size());
        assertSame(list, result);
        verify(authorDao).findAll();
    }

    @Test
    void existsById_ShouldReturnTrue_WhenDaoReturnsTrue() {
        when(authorDao.existsById(7L)).thenReturn(true);

        assertTrue(service.existsById(7L));
        verify(authorDao).existsById(7L);
    }
}
