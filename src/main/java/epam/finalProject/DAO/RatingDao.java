package epam.finalProject.DAO;

import epam.finalProject.entity.Rating;

import java.util.List;

public interface RatingDao {
    boolean save(Rating rating);
    List<Rating> findByBookId(Long bookId);
    List<Rating> findByUserId(Long userId);
}

