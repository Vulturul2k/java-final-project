package app.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DonationOfferTest {

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        DonationOffer donationOffer = new DonationOffer();

        // Act
        donationOffer.setDonationMessage("Please enjoy this book!");
        donationOffer.setIsCharity(true);
        donationOffer.setPickupRequired(true);

        // Assert
        assertEquals("Please enjoy this book!", donationOffer.getDonationMessage());
        assertTrue(donationOffer.getIsCharity());
        assertTrue(donationOffer.getPickupRequired());
    }

    @Test
    void defaultValues_ShouldBeFalse() {
        // Arrange & Act
        DonationOffer donationOffer = new DonationOffer();

        // Assert
        assertFalse(donationOffer.getIsCharity());
        assertFalse(donationOffer.getPickupRequired());
    }
}
