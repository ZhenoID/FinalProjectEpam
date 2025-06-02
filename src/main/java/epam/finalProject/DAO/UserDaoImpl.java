package epam.finalProject.DAO;

import epam.finalProject.entity.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import epam.finalProject.db.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private final DataSource ds;

    // Конструктор для тестов
    public UserDaoImpl(DataSource ds) {
        this.ds = ds;
    }

    // Продакшен-конструктор
    public UserDaoImpl() {
        this.ds = null;
    }

    private Connection getConnection() throws SQLException {
        if (ds != null) {
            return ds.getConnection();
        } else {
            return ConnectionPool.getInstance().getConnection();
        }
    }


    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in save()", e);
            return false;
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId((long) rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            logger.error("Database error in findByUsername()", e);
            return null;
        }
        return null;
    }
    @Override
    public boolean updatePassword(User user) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getUsername());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in update()", e);
            return false;
        }
    }

    @Override
    public List<User> findAll(){
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                user.setUsername(rs.getString("username"));
                users.add(user);
            }
        }catch(SQLException e){
            logger.error("Database error in findAll()", e);
            return null;
        }
    return users;
    }

    @Override
    public boolean delete(User user){
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in delete()", e);
            return false;
        }
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setPassword(rs.getString("password"));
                return user;
            }
        } catch (SQLException e) {
            logger.error("Database error in findById()", e);
            return null;
        }
        return null;
    }


    @Override
    public boolean updateRole(Long id, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Database error in updateRole()", e);
            return false;
        }
    }
}