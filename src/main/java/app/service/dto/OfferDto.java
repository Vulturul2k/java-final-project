
package app.service.dto;

import java.util.List;
import java.util.UUID;

public class OfferDto {
    private UUID id;
    private String senderEmail;
    private String receiverEmail;
    private List<String> offeredBookTitles;
    private List<String> requestedBookTitles;
    private String status;
    private String offerType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<String> getOfferedBookTitles() {
        return offeredBookTitles;
    }

    public void setOfferedBookTitles(List<String> offeredBookTitles) {
        this.offeredBookTitles = offeredBookTitles;
    }

    public List<String> getRequestedBookTitles() {
        return requestedBookTitles;
    }

    public void setRequestedBookTitles(List<String> requestedBookTitles) {
        this.requestedBookTitles = requestedBookTitles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getOfferType() {
        return offerType;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }
}
