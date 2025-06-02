package epam.finalProject.service;

import epam.finalProject.DAO.PurchaseHistoryDao;
import epam.finalProject.DAO.PurchaseHistoryDaoImpl;
import epam.finalProject.entity.PurchaseHistory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseHistoryServiceImpl implements PurchaseHistoryService {
    private final PurchaseHistoryDao historyDao = new PurchaseHistoryDaoImpl();

    @Override
    public boolean record(PurchaseHistory ph) {
        return historyDao.save(ph);
    }

    @Override
    public List<PurchaseHistory> getByUserId(Long userId) {
        return historyDao.findByUserId(userId);
    }
}
