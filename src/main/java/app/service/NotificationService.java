package app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Async("taskExecutor")
    public CompletableFuture<Void> sendOfferCreatedNotification(String receiverEmail, String senderEmail) {
        logger.info("[ASYNC] Sending offer notification to {} from {} (Thread: {})",
                receiverEmail, senderEmail, Thread.currentThread().getName());

        // Simulate notification processing time
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("[ASYNC] Offer notification sent successfully to {}", receiverEmail);
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> sendOfferAcceptedNotification(String senderEmail, String receiverEmail) {
        logger.info("[ASYNC] Sending acceptance notification to {} (accepted by {}) (Thread: {})",
                senderEmail, receiverEmail, Thread.currentThread().getName());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("[ASYNC] Acceptance notification sent to {}", senderEmail);
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<Void> sendOfferRejectedNotification(String senderEmail, String receiverEmail) {
        logger.info("[ASYNC] Sending rejection notification to {} (rejected by {}) (Thread: {})",
                senderEmail, receiverEmail, Thread.currentThread().getName());

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.info("[ASYNC] Rejection notification sent to {}", senderEmail);
        return CompletableFuture.completedFuture(null);
    }

    @Async("taskExecutor")
    public CompletableFuture<String> processOfferAnalytics(String offerId) {
        logger.info("[ASYNC] Processing analytics for offer {} (Thread: {})",
                offerId, Thread.currentThread().getName());

        try {
            Thread.sleep(1000); // Simulate heavy processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String result = "Analytics completed for offer: " + offerId;
        logger.info("[ASYNC] {}", result);
        return CompletableFuture.completedFuture(result);
    }
}
