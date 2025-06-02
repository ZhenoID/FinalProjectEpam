package epam.finalProject.DAO;

import epam.finalProject.db.ConnectionPool;
import epam.finalProject.entity.PurchaseHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseHistoryDaoImpl implements PurchaseHistoryDao {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseHistoryDaoImpl.class);

    private Connection getConnection() throws SQLException {
        return ConnectionPool.getInstance().getConnection();
    }

    @Override
    public boolean save(PurchaseHistory record) {
        String sql = "INSERT INTO purchase_history (user_id, book_id, quantity, purchase_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, record.getUserId());
            ps.setLong(2, record.getBookId());
            ps.setInt(3, record.getQuantity());
            ps.setTimestamp(4, record.getPurchaseDate());
            int affected = ps.executeUpdate();
            if (affected == 0) {
                return false;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    record.setId(keys.getLong(1));
                }
            }
            return true;
        } catch (SQLException e) {
            logger.error("Database error in save(PurchaseHistory)", e);
            return false;
        }
    }

    @Override
    public List<PurchaseHistory> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, book_id, quantity, purchase_date " +
                "FROM purchase_history WHERE user_id = ? ORDER BY purchase_date DESC";
        List<PurchaseHistory> result = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PurchaseHistory ph = new PurchaseHistory();
                    ph.setId(rs.getLong("id"));
                    ph.setUserId(rs.getLong("user_id"));
                    ph.setBookId(rs.getLong("book_id"));
                    ph.setQuantity(rs.getInt("quantity"));

                    Timestamp ts = rs.getTimestamp("purchase_date");
                    ph.setPurchaseDate(ts);  // присваиваем Timestamp напрямую

                    result.add(ph);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error in findByUserId(PurchaseHistory)", e);
        }
        return result;
    }


}
