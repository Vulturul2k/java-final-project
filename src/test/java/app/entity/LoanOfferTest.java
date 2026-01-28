package app.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class LoanOfferTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        LoanOffer loanOffer = new LoanOffer();
        LocalDateTime returnDate = LocalDateTime.now().plusDays(30);

        // Act
        loanOffer.setLoanDurationDays(30);
        loanOffer.setReturnDate(returnDate);
        loanOffer.setLateFeePerDay(2.5);
        loanOffer.setDepositRequired(true);

        // Assert
        assertEquals(30, loanOffer.getLoanDurationDays());
        assertEquals(returnDate, loanOffer.getReturnDate());
        assertEquals(2.5, loanOffer.getLateFeePerDay());
        assertTrue(loanOffer.getDepositRequired());
    }

    @Test
    void depositRequired_ShouldDefaultToFalse() {
        // Arrange & Act
        LoanOffer loanOffer = new LoanOffer();

        // Assert
        assertFalse(loanOffer.getDepositRequired());
    }
}
