package app.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OfferedBookTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        OfferedBook offeredBook = new OfferedBook();
        Offer offer = new BookExchangeOffer();
        Book book = new Book();

        // Act
        offeredBook.setOffer(offer);
        offeredBook.setBook(book);
        offeredBook.setRequested(true);

        // Assert
        assertSame(offer, offeredBook.getOffer());
        assertSame(book, offeredBook.getBook());
        assertTrue(offeredBook.isRequested());
    }

    @Test
    void isRequested_ShouldDefaultToFalse() {
        // Arrange & Act
        OfferedBook offeredBook = new OfferedBook();

        // Assert
        assertFalse(offeredBook.isRequested());
    }
}
