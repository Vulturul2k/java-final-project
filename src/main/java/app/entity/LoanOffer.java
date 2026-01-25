package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_offer", schema = "project")
@DiscriminatorValue("LOAN")
public class LoanOffer extends Offer {

    @Column(name = "loan_duration_days")
    private Integer loanDurationDays; // Duration of the loan in days

    @Column(name = "return_date")
    private LocalDateTime returnDate; // Expected return date

    @Column(name = "late_fee_per_day")
    private Double lateFeePerDay; // Optional late fee

    @Column(name = "deposit_required")
    private Boolean depositRequired = false; // Whether a deposit is required

    public Integer getLoanDurationDays() {
        return loanDurationDays;
    }

    public void setLoanDurationDays(Integer loanDurationDays) {
        this.loanDurationDays = loanDurationDays;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
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
