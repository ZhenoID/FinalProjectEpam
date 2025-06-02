package epam.finalProject.DAO;

import epam.finalProject.db.ConnectionPool;
import epam.finalProject.entity.BasketItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasketDaoImpl implements BasketDao {
    private static final Logger logger = LoggerFactory.getLogger(BasketDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    @Override
    public boolean addOrUpdateQuantity(Long userId, Long bookId, int delta) {
        String updateSql = "UPDATE basket_items SET quantity = quantity + ? " +
                "WHERE user_id = ? AND book_id = ?";
        String insertSql = "INSERT INTO basket_items (user_id, book_id, quantity) VALUES (?, ?, ?)";

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setInt(1, delta);
                psUpdate.setLong(2, userId);
                psUpdate.setLong(3, bookId);
                int updated = psUpdate.executeUpdate();
                if (updated > 0) {
                    String deleteIfZero = "DELETE FROM basket_items WHERE user_id = ? AND book_id = ? AND quantity <= 0";
                    try (PreparedStatement psDel = conn.prepareStatement(deleteIfZero)) {
                        psDel.setLong(1, userId);
                        psDel.setLong(2, bookId);
                        psDel.executeUpdate();
                    }
                    conn.commit();
                    return true;
                }
            }

            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setLong(1, userId);
                psInsert.setLong(2, bookId);
                psInsert.setInt(3, delta);
                psInsert.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            logger.error("Database error in addOrUpdateQuantity()", e);
            return false;
        }
    }

    @Override
    public boolean setQuantity(Long userId, Long bookId, int newQuantity) {
        if (newQuantity <= 0) {
            return deleteItem(userId, bookId);
        }
        String sql = "UPDATE basket_items SET quantity = ? WHERE user_id = ? AND book_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setLong(2, userId);
            ps.setLong(3, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in setQuantity()", e);
            return false;
        }
    }

    @Override
    public List<BasketItem> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, book_id, quantity FROM basket_items WHERE user_id = ? ORDER BY id";
        List<BasketItem> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BasketItem item = new BasketItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setBookId(rs.getLong("book_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    result.add(item);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error in findByUserId()", e);
        }
        return result;
    }

    @Override
    public boolean deleteItem(Long userId, Long bookId) {
        String sql = "DELETE FROM basket_items WHERE user_id = ? AND book_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in deleteItem()", e);
            return false;
        }
    }

    @Override
    public boolean deleteAllByUserId(Long userId) {
        String sql = "DELETE FROM basket_items WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in deleteAllByUserId()", e);
            return false;
        }
    }
}
