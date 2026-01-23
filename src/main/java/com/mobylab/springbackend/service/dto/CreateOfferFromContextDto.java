
package com.mobylab.springbackend.service.dto;

import java.util.List;
import java.util.UUID;

public class CreateOfferFromContextDto {
    private String receiverEmail;
    private List<String> offeredBookTitles;
    private List<String> requestedBookTitles;
    private String offerType;

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
}
