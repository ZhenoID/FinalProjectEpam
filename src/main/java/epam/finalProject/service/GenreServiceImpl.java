package epam.finalProject.service;

import epam.finalProject.DAO.GenreDao;
import epam.finalProject.DAO.GenreDaoImpl;
import epam.finalProject.entity.Genre;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    private final GenreDao genreDao = new GenreDaoImpl();

    @Override
    public boolean save(Genre genre) {
        return genreDao.save(genre);
    }

    @Override
    public List<Genre> findAll() {
        return genreDao.findAll();
    }

    @Override
    public Genre findById(Long id) {
        return genreDao.findById(id);
    }
}
