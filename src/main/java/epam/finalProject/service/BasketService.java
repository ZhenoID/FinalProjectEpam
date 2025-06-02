package epam.finalProject.service;

import epam.finalProject.entity.BasketItem;
import java.util.List;

public interface BasketService {
    /**
     * Добавить (или увеличить) количество книг в корзине:
     * delta > 0 → увеличить/добавить указанное количество,
     * delta < 0 → уменьшить указанное количество (если после уменьшения <=0, строка удаляется).
     */
    boolean changeQuantity(Long userId, Long bookId, int delta);

    /** Явно установить точное количество (>=0).*/
    boolean setQuantity(Long userId, Long bookId, int newQuantity);

    /** Удалить строку (все копии книги) из корзины. */
    boolean removeItem(Long userId, Long bookId);

    /** Очистить всё. */
    boolean clearBasket(Long userId);

    /** Получить содержимое корзины (список BasketItem). */
    List<BasketItem> getBasketItems(Long userId);

    boolean confirmAll(Long userId);
}
