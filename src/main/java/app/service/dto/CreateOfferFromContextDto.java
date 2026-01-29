
package app.service.dto;

import java.util.List;
import java.util.UUID;

public class CreateOfferFromContextDto {
    private String receiverEmail;
    private List<String> offeredBookTitles;
    private List<String> requestedBookTitles;
    private String offerType;

    private String exchangeNotes;
    private Boolean isNegotiable;

    private String donationMessage;
    private Boolean isCharity;
    private Boolean pickupRequired;

    private Integer loanDurationDays;
    private String returnDate;
    private Double lateFeePerDay;
    private Boolean depositRequired;

    public List<String> getRequestedBookTitles() {
        return requestedBookTitles;
    }

    public void setRequestedBookTitles(List<String> requestedBookTitles) {
        this.requestedBookTitles = requestedBookTitles;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public List<String> getOfferedBookTitles() {
        return offeredBookTitles;
    }

    public void setOfferedBookTitles(List<String> offeredBookTitles) {
        this.offeredBookTitles = offeredBookTitles;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getExchangeNotes() {
        return exchangeNotes;
    }

    public void setExchangeNotes(String exchangeNotes) {
        this.exchangeNotes = exchangeNotes;
    }

    public Boolean getIsNegotiable() {
        return isNegotiable;
    }

    public void setIsNegotiable(Boolean negotiable) {
        isNegotiable = negotiable;
    }

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

    public Integer getLoanDurationDays() {
        return loanDurationDays;
    }

    public void setLoanDurationDays(Integer loanDurationDays) {
        this.loanDurationDays = loanDurationDays;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public Double getLateFeePerDay() {
        return lateFeePerDay;
    }

    public void setLateFeePerDay(Double lateFeePerDay) {
        this.lateFeePerDay = lateFeePerDay;
    }

    public Boolean getDepositRequired() {
        return depositRequired;
    }

    public void setDepositRequired(Boolean depositRequired) {
        this.depositRequired = depositRequired;
    }
}
