package epam.finalProject.service;

import epam.finalProject.DAO.AuthorDao;
import epam.finalProject.DAO.AuthorDaoImpl;
import epam.finalProject.entity.Author;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorDao authorDao;

    public AuthorServiceImpl() {
        this(new AuthorDaoImpl());
    }

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    public boolean save(Author author) {
        return authorDao.save(author);
    }

    @Override
    public boolean update(Author author) {
        return authorDao.update(author);
    }

    @Override
    public boolean delete(Long id) {
        return authorDao.delete(id);
    }

    @Override
    public Author findById(Long id) {
        return authorDao.findById(id);
    }

    @Override
    public List<Author> findAll() {
        return authorDao.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return authorDao.existsById(id);
    }

}
