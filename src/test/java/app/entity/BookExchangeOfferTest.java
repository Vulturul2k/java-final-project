package app.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookExchangeOfferTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        BookExchangeOffer offer = new BookExchangeOffer();

        // Act
        offer.setExchangeNotes("Looking forward to the exchange");
        offer.setIsNegotiable(false);

        // Assert
        assertEquals("Looking forward to the exchange", offer.getExchangeNotes());
        assertFalse(offer.getIsNegotiable());
    }

    @Test
    void isNegotiable_ShouldDefaultToTrue() {
        // Arrange & Act
        BookExchangeOffer offer = new BookExchangeOffer();

        // Assert
        assertTrue(offer.getIsNegotiable());
    }
}
