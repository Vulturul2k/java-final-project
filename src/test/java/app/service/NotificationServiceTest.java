package app.service;

import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    void sendOfferCreatedNotification_ShouldReturnCompletedFuture() {
        // Act
        CompletableFuture<Void> future = notificationService.sendOfferCreatedNotification("u1", "u2");

        // Assert
        assertNotNull(future);
    }

    @Test
    void sendOfferAcceptedNotification_ShouldReturnCompletedFuture() {
        // Act
        CompletableFuture<Void> future = notificationService.sendOfferAcceptedNotification("u1", "u2");

        // Assert
        assertNotNull(future);
    }

    @Test
    void sendOfferRejectedNotification_ShouldReturnCompletedFuture() {
        // Act
        CompletableFuture<Void> future = notificationService.sendOfferRejectedNotification("u1", "u2");

        // Assert
        assertNotNull(future);
    }

    @Test
    void processOfferAnalytics_ShouldReturnCompletedFutureWithResult() {
        // Act
        CompletableFuture<String> future = notificationService.processOfferAnalytics("offer123");

        // Assert
        assertNotNull(future);
        String result = future.join();
        assertTrue(result.contains("offer123"));
    }
}
