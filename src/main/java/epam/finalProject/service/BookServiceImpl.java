package epam.finalProject.service;

import epam.finalProject.DAO.BookDao;
import epam.finalProject.DAO.BookDaoImpl;
import epam.finalProject.entity.Author;
import epam.finalProject.entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookDao bookDao;

    public BookServiceImpl() {
        this(new BookDaoImpl());
    }

    public BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }
    @Override
    public boolean deleteBook(Book book) {
        return bookDao.deleteBook(book);
    }

    @Override
    public boolean changeBook(Book book) {
        return bookDao.changeBook(book);
    }

    @Override
    public List<Book> findAll() {
        return bookDao.findAll();
    }

    @Override
    public Book findById(Long id) {
        return bookDao.findById(id);
    }

    @Override
    public boolean saveBookWithAuthor(Book book, Author author){
        return bookDao.saveBookWithAuthor(book, author);
    }

}
