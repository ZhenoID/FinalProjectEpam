package epam.finalProject.controller;

import epam.finalProject.entity.Author;
import epam.finalProject.service.AuthorService;
import epam.finalProject.service.AuthorServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService = new AuthorServiceImpl();

    @GetMapping
    public String listAuthors(Model model) {
        List<Author> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors";
    }

    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("author", new Author());
        return "authorForm";
    }

    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute Author author) {
        if (author.getId() == null) {
            authorService.save(author);
        } else {
            authorService.update(author);
        }
        return "redirect:/authors";
    }

    @GetMapping("/edit/{id}")
    public String editAuthor(@PathVariable Long id, Model model) {
        Author author = authorService.findById(id);
        model.addAttribute("author", author);
        return "authorForm";
    }

    @GetMapping("/delete/{id}")
    public String deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return "redirect:/authors";
    }
}
