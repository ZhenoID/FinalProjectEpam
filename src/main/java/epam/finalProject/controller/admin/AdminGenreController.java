package epam.finalProject.controller.admin;

import epam.finalProject.entity.Genre;
import epam.finalProject.service.GenreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequestMapping("/admin/genres")
@PreAuthorize("hasAnyAuthority('ADMIN','LIBRARIAN')")
public class AdminGenreController {
    private final GenreService genreService;
    public AdminGenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("genre", new Genre());
        return "admin/add-genre";
    }

    @PostMapping("/add")
    public String addGenre(@ModelAttribute Genre genre) {
        genreService.save(genre);
        return "redirect:/admin/books/add";
    }

    @GetMapping
    public String listGenres(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "admin/genre-list";
    }
}
