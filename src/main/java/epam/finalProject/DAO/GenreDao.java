package epam.finalProject.DAO;

import epam.finalProject.entity.Genre;

import java.util.List;

public interface GenreDao {
    boolean save(Genre genre);
    Genre findById(Long id);
    List<Genre> findAll();
}
