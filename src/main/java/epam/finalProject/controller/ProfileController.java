package epam.finalProject.controller;

import epam.finalProject.entity.User;
import epam.finalProject.service.UserService;
import epam.finalProject.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class ProfileController {
    private final UserService userService = new UserServiceImpl();

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.getByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute User user, HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null || username.isEmpty()) {
            return "redirect:/login";
        }

        user.setUsername(username);
        String hashed = org.mindrot.jbcrypt.BCrypt.hashpw(user.getPassword(), org.mindrot.jbcrypt.BCrypt.gensalt());
        user.setPassword(hashed);

        userService.updatePassword(user);
        return "redirect:/profile";
    }

    @GetMapping("/profile/settings")
    public String showSettings() {
        return "profileSettings";
    }

    @PostMapping("/profile/settings")
    public String updatePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String repeatNewPassword,
            Principal principal,
            Model model) {

        // 1) Получаем имя пользователя напрямую из Principal
        String username = principal.getName();
        if (username == null) {
            return "redirect:/login";
        }

        User user = userService.getByUsername(username);
        // 2) Сравниваем введённый старый пароль с тем, что хранился в базе:
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            model.addAttribute("error", "Old password is incorrect");
            return "profileSettings";
        }
        if (!newPassword.equals(repeatNewPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "profileSettings";
        }
        // 3) Генерируем хэш нового пароля и обновляем у пользователя
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPassword(hashed);
        userService.updatePassword(user);

        model.addAttribute("message", "Password updated successfully");
        return "profileSettings";
    }


}
