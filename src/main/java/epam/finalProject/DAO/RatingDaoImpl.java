package epam.finalProject.DAO;

import epam.finalProject.db.ConnectionPool;
import epam.finalProject.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDaoImpl implements RatingDao {

    @Override
    public boolean save(Rating rating) {
        String sql = "INSERT INTO ratings (user_id, book_id, score) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, rating.getUserId());
            ps.setLong(2, rating.getBookId());
            ps.setInt(3, rating.getScore());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Rating> findByBookId(Long bookId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE book_id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bookId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rating rating = new Rating();
                rating.setId(rs.getLong("id"));
                rating.setUserId(rs.getLong("user_id"));
                rating.setBookId(rs.getLong("book_id"));
                rating.setScore(rs.getInt("score"));
                ratings.add(rating);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }

    @Override
    public List<Rating> findByUserId(Long userId) {
        List<Rating> ratings = new ArrayList<>();
        String sql = "SELECT * FROM ratings WHERE user_id = ?";
        try (Connection conn = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Rating rating = new Rating();
                rating.setId(rs.getLong("id"));
                rating.setUserId(rs.getLong("user_id"));
                rating.setBookId(rs.getLong("book_id"));
                rating.setScore(rs.getInt("score"));
                ratings.add(rating);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratings;
    }
}
