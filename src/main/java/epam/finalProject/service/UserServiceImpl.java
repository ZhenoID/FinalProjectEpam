package epam.finalProject.service;

import epam.finalProject.DAO.UserDao;
import epam.finalProject.DAO.UserDaoImpl;
import epam.finalProject.entity.User;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDAO;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDAO) {
        this.userDAO = userDAO;
    }
    @Override
    public boolean register(User user) {
        if (userDAO.findByUsername(user.getUsername()) != null) return false;
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        user.setRole("USER");
        return userDAO.save(user);
    }



    @Override
    public User getByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public boolean updatePassword(User user) {
        return userDAO.updatePassword(user);
    }

    @Override
    public boolean updateRole(Long userId, String newRole){return userDAO.updateRole(userId, newRole);}

    @Override
    public List<User> findAll(){
        return userDAO.findAll();
    }

    @Override
    public boolean deleteUser(User user){
        return userDAO.delete(user);
    }

    @Override
    public User getById(Long id) {
        return userDAO.findById(id);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User user = userDAO.findByUsername(username);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }

}
