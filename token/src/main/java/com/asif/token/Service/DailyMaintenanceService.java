package com.asif.token.Service;

import com.asif.token.Entity.ShopToken;
import com.asif.token.Entity.Shops;
import com.asif.token.Repository.ShopRepository;
import com.asif.token.Repository.ShopTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class DailyMaintenanceService {

    @Autowired
    private ShopRepository shopRepo;

    @Autowired
    private ShopTokenRepository shopTokenRepo; // ✅ Correct repository

    // ✅ Run daily at midnight
    @Scheduled(cron = "0 0 0 * * ?") // 12:00 AM
    public void resetAndCleanTokens() {
        resetShops();
        cleanExpiredTokens();
    }

    private void resetShops() {
        List<Shops> shops = shopRepo.findAll();
        for (Shops shop : shops) {
            shop.setCurrentToken(1);
            shop.setTotalToken(0);
        }
        shopRepo.saveAll(shops);
        System.out.println("♻️ All shops reset successfully at midnight");
    }

    private void cleanExpiredTokens() {
        List<ShopToken> allTokens = shopTokenRepo.findAll(); // ✅ fixed
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        int deletedCount = 0;

        for (ShopToken token : allTokens) {
            Shops shop = token.getShop();
            if (shop.getCloseTime() != null) {
                LocalTime closeTime = LocalTime.parse(shop.getCloseTime());
                if (now.isAfter(closeTime) || !token.getCreatedAt().toLocalDate().isEqual(today)) {
                    shopTokenRepo.delete(token);
                    deletedCount++;
                }
            }
        }
        System.out.println("🧹 Deleted " + deletedCount + " expired tokens.");
    }
}
