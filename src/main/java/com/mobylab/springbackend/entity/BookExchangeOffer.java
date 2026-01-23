package com.mobylab.springbackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "book_exchange_offer", schema = "project")
@DiscriminatorValue("EXCHANGE")
public class BookExchangeOffer extends Offer {

    @Column(name = "exchange_notes")
    private String exchangeNotes; // Additional notes about the exchange

    @Column(name = "is_negotiable")
    private Boolean isNegotiable = true; // Whether the exchange terms are negotiable

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
}
