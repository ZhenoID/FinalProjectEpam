package epam.finalProject.controller;

import epam.finalProject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import epam.finalProject.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model){
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id){
        User user = userService.getById(id);
        if(user != null) {
            userService.deleteUser(user);
        }
        return "redirect:/admin/users";
    }
}
