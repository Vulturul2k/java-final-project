-- V2: Add Offer Hierarchy
-- This migration adds support for different types of offers: Exchange, Donation, and Loan

SET search_path = project, pg_catalog;

-- Add discriminator column to offer table
ALTER TABLE project.offer ADD COLUMN offer_type VARCHAR(50);

-- Set default value for existing offers
UPDATE project.offer SET offer_type = 'EXCHANGE' WHERE offer_type IS NULL;

-- Create book_exchange_offer table (for standard exchanges)
CREATE TABLE IF NOT EXISTS project.book_exchange_offer (
    id UUID PRIMARY KEY,
    exchange_notes TEXT,
    is_negotiable BOOLEAN DEFAULT true,
    FOREIGN KEY (id) REFERENCES project.offer(id) ON DELETE CASCADE
);

-- Create donation_offer table (for free donations)
CREATE TABLE IF NOT EXISTS project.donation_offer (
    id UUID PRIMARY KEY,
    donation_message TEXT,
    is_charity BOOLEAN DEFAULT false,
    pickup_required BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES project.offer(id) ON DELETE CASCADE
);

-- Create loan_offer table (for temporary loans)
CREATE TABLE IF NOT EXISTS project.loan_offer (
    id UUID PRIMARY KEY,
    loan_duration_days INTEGER,
    return_date TIMESTAMP,
    late_fee_per_day DECIMAL(10, 2),
    deposit_required BOOLEAN DEFAULT false,
    FOREIGN KEY (id) REFERENCES project.offer(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_offer_type ON project.offer(offer_type);
CREATE INDEX IF NOT EXISTS idx_loan_return_date ON project.loan_offer(return_date);

-- Add comments for documentation
COMMENT ON COLUMN project.offer.offer_type IS 'Type of offer: EXCHANGE, DONATION, or LOAN';
COMMENT ON TABLE project.book_exchange_offer IS 'Standard book exchange between users';
COMMENT ON TABLE project.donation_offer IS 'Free book donation without expecting return';
COMMENT ON TABLE project.loan_offer IS 'Temporary book loan with return date';
