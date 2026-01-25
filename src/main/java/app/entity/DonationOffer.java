package app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "donation_offer", schema = "project")
@DiscriminatorValue("DONATION")
public class DonationOffer extends Offer {

    @Column(name = "donation_message")
    private String donationMessage;

    @Column(name = "is_charity")
    private Boolean isCharity = false;

    @Column(name = "pickup_required")
    private Boolean pickupRequired = false;

    public String getDonationMessage() {
        return donationMessage;
    }

    public void setDonationMessage(String donationMessage) {
        this.donationMessage = donationMessage;
    }

    public Boolean getIsCharity() {
        return isCharity;
    }

    public void setIsCharity(Boolean charity) {
        isCharity = charity;
    }

    public Boolean getPickupRequired() {
        return pickupRequired;
    }

    public void setPickupRequired(Boolean pickupRequired) {
        this.pickupRequired = pickupRequired;
    }
}
