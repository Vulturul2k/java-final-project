package app.service.dto;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferDtoTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        OfferDto dto = new OfferDto();
        UUID id = UUID.randomUUID();
        List<String> offered = List.of("Book1", "Book2");
        List<String> requested = List.of("Book3");

        // Act
        dto.setId(id);
        dto.setSenderEmail("sender@test.com");
        dto.setReceiverEmail("receiver@test.com");
        dto.setOfferedBookTitles(offered);
        dto.setRequestedBookTitles(requested);
        dto.setStatus("PENDING");
        dto.setOfferType("EXCHANGE");

        // Assert
        assertEquals(id, dto.getId());
        assertEquals("sender@test.com", dto.getSenderEmail());
        assertEquals("receiver@test.com", dto.getReceiverEmail());
        assertEquals(offered, dto.getOfferedBookTitles());
        assertEquals(requested, dto.getRequestedBookTitles());
        assertEquals("PENDING", dto.getStatus());
        assertEquals("EXCHANGE", dto.getOfferType());
    }
}
