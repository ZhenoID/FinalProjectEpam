package epam.finalProject.controller;

import epam.finalProject.entity.PurchaseHistory;
import epam.finalProject.entity.Book;
import epam.finalProject.service.PurchaseHistoryService;
import epam.finalProject.service.BookService;
import epam.finalProject.service.UserService;
import epam.finalProject.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class PurchaseHistoryController {

    private final PurchaseHistoryService historyService;
    private final BookService            bookService;
    private final UserService            userService;

    public PurchaseHistoryController(PurchaseHistoryService historyService,
                                     BookService bookService,
                                     UserService userService) {
        this.historyService = historyService;
        this.bookService    = bookService;
        this.userService    = userService;
    }

    @GetMapping("/purchase-history")
    public String viewHistory(Authentication auth, Model model) {
        if (auth == null || auth.getName() == null) {
            return "redirect:/login";
        }
        User user = userService.getByUsername(auth.getName());
        if (user == null) {
            return "redirect:/login";
        }

        List<PurchaseHistory> raw = historyService.getByUserId(user.getId());

        List<Object[]> historyRows = raw.stream().map(ph -> {
            Book book = bookService.findById(ph.getBookId());
            return new Object[]{ book, ph.getQuantity(), ph.getPurchaseDate() };
        }).collect(Collectors.toList());

        model.addAttribute("historyRows", historyRows);
        return "purchase-history";
    }
}
