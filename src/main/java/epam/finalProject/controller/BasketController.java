package epam.finalProject.controller;

import epam.finalProject.entity.BasketItem;
import epam.finalProject.entity.Book;
import epam.finalProject.entity.User;
import epam.finalProject.service.BasketService;
import epam.finalProject.service.BookService;
import epam.finalProject.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;
    private final UserService userService;
    private final BookService bookService;

    public BasketController(BasketService basketService,
                            UserService userService,
                            BookService bookService) {
        this.basketService = basketService;
        this.userService = userService;
        this.bookService = bookService;
    }

    /** Добавить delta копий (может быть +1, -1) книги в корзину */
    @PostMapping("/change/{bookId}/{delta}")
    public String changeQuantity(@PathVariable Long bookId,
                                 @PathVariable int delta,
                                 Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        if (user != null) {
            basketService.changeQuantity(user.getId(), bookId, delta);
        }
        return "redirect:/basket";
    }

    /** Установить точное количество (например, из формы ввода числа) */
    @PostMapping("/set/{bookId}")
    public String setQuantity(@PathVariable Long bookId,
                              @RequestParam int quantity,
                              Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        if (user != null) {
            basketService.setQuantity(user.getId(), bookId, quantity);
        }
        return "redirect:/basket";
    }

    /** Удалить всю строку для одной книги */
    @PostMapping("/remove/{bookId}")
    public String removeItem(@PathVariable Long bookId,
                             Authentication auth) {
        User user = userService.getByUsername(auth.getName());
        if (user != null) {
            basketService.removeItem(user.getId(), bookId);
        }
        return "redirect:/basket";
    }

    /** Просмотр корзины */
    @GetMapping
    public String viewBasket(Authentication auth, Model model) {
        User user = userService.getByUsername(auth.getName());
        List<BasketItem> items = List.of();
        if (user != null) {
            items = basketService.getBasketItems(user.getId());
        }

        List<Book> booksInBasket = items.stream()
                .map(it -> {
                    Book b = bookService.findById(it.getBookId());
                    if (b != null) {
                        b.setQuantity(it.getQuantity());
                    }
                    return b;
                })
                .collect(Collectors.toList());

        model.addAttribute("booksInBasket", booksInBasket);
        return "basket";
    }

    /** Подтвердить покупку всей корзины */
    @PostMapping("/confirm")
    public String confirmAll(Authentication auth, Model model) {
        User user = userService.getByUsername(auth.getName());
        boolean success = false;
        if (user != null) {
            success = basketService.confirmAll(user.getId());
        }
        if (!success) {
            model.addAttribute("error", "Возникла ошибка: проверьте наличие достаточного количества книг.");
            return viewBasket(auth, model);
        }
        return "redirect:/purchase-history";
    }
}
