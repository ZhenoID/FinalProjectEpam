package epam.finalProject.DAO;

import epam.finalProject.entity.BasketItem;
import java.util.List;

public interface BasketDao {
    /**
     * Добавить указанное количество книг в корзину.
     * Если пар user+book ещё нет, создать новую запись с этим quantity.
     * Если запись существует, увеличить quantity на указанное.
     */
    boolean addOrUpdateQuantity(Long userId, Long bookId, int delta);

    /**
     * Явно установить новое количество (например, при редактировании корзины).
     * Если newQuantity > 0, обновляем;
     * Если newQuantity <= 0, удаляем запись.
     */
    boolean setQuantity(Long userId, Long bookId, int newQuantity);

    /** Получить все строки корзины (с положительным quantity) для данного пользователя */
    List<BasketItem> findByUserId(Long userId);

    /** Полностью удалить одну строку (bookId) из корзины пользователя */
    boolean deleteItem(Long userId, Long bookId);

    /** Полностью очистить корзину пользователя (удалить все записи) */
    boolean deleteAllByUserId(Long userId);
}
