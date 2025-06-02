package epam.finalProject.service;

import epam.finalProject.DAO.*;
import epam.finalProject.entity.BasketItem;
import epam.finalProject.entity.PurchaseHistory;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class BasketServiceImpl implements BasketService {

    private final BasketDao basketDao = new BasketDaoImpl();
    private final BookDao bookDao = new BookDaoImpl();
    private final PurchaseHistoryDao historyDao = new PurchaseHistoryDaoImpl();

    @Override
    public boolean changeQuantity(Long userId, Long bookId, int delta) {
        // До изменения количества убедимся, что в наличии достаточно книг
        // Получим текущую запись о книге и проверим, сколько доступно в библиотеке:
        int currentBasketQty = 0;
        List<BasketItem> items = basketDao.findByUserId(userId);
        for (BasketItem it : items) {
            if (it.getBookId().equals(bookId)) {
                currentBasketQty = it.getQuantity();
                break;
            }
        }
        int desiredQty = currentBasketQty + delta;
        if (delta > 0) {
            // Проверяем, что в библиотеке есть хотя бы delta штук:
            // Предполагаем, что BookDao имеет метод findById, где есть поле quantity
            epam.finalProject.entity.Book book = bookDao.findById(bookId);
            if (book == null || book.getQuantity() < delta) {
                return false; // не хватает копий
            }
        }
        // Если desiredQty <= 0, то будет удалена сама строка
        return basketDao.addOrUpdateQuantity(userId, bookId, delta);
    }

    @Override
    public boolean setQuantity(Long userId, Long bookId, int newQuantity) {
        if (newQuantity < 0) newQuantity = 0;
        // Проверяем наличие
        epam.finalProject.entity.Book book = bookDao.findById(bookId);
        if (book == null || book.getQuantity() < newQuantity) {
            return false;
        }
        return basketDao.setQuantity(userId, bookId, newQuantity);
    }

    @Override
    public boolean removeItem(Long userId, Long bookId) {
        return basketDao.deleteItem(userId, bookId);
    }

    @Override
    public boolean clearBasket(Long userId) {
        return basketDao.deleteAllByUserId(userId);
    }

    @Override
    public List<BasketItem> getBasketItems(Long userId) {
        return basketDao.findByUserId(userId);
    }


    public boolean confirmAll(Long userId) {
        List<BasketItem> items = basketDao.findByUserId(userId);
        boolean ok = true;
        for (BasketItem it : items) {
            // проверим, что ещё есть в библиотеке нужное количество:
            epam.finalProject.entity.Book book = bookDao.findById(it.getBookId());
            if (book == null || book.getQuantity() < it.getQuantity()) {
                ok = false;
                break;
            }
        }
        if (!ok) return false;

        // Теперь выполним транзакционно: уменьшаем остатки + пишем в историю + удаляем из корзины
        for (BasketItem it : items) {
            bookDao.decrementQuantity(it.getBookId(), it.getQuantity());
            // добавляем в history
            PurchaseHistory record = new PurchaseHistory(
                    userId, it.getBookId(), it.getQuantity(), new Timestamp(System.currentTimeMillis()));
            historyDao.save(record);
            // удаляем из корзины
            basketDao.deleteItem(userId, it.getBookId());
        }
        return true;
    }

    // При необходимости можно добавить отдельный метод confirmOne(userId, bookId),
    // если хочется подтверждать покупку для одной строки корзины,
    // но обычно делают «купить всё, что в корзине» через confirmAll(...)


}
