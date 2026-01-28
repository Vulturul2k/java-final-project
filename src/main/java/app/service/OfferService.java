package app.service;

import app.entity.*;
import app.repository.*;
import app.service.dto.CreateOfferFromContextDto;
import app.service.dto.OfferDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OfferedBookRepository offeredBookRepository;
    @Autowired
    private BookServie bookServie;
    @Autowired
    private NotificationService notificationService;

    public OfferDto createFromAuthenticatedUser(CreateOfferFromContextDto dto, String email) {
        User sender = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepository.findUserByEmail(dto.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        List<UUID> offeredBooks = resolveBookIdsFromTitles(dto.getOfferedBookTitles(), sender.getId());
        List<UUID> requestedBooks = resolveBookIdsFromTitles(dto.getRequestedBookTitles(), receiver.getId());

        String offerType = dto.getOfferType() != null ? dto.getOfferType() : "EXCHANGE";
        return createOffer(sender, receiver, offeredBooks, requestedBooks, offerType);
    }

    public OfferDto createOffer(User sender, User receiver, List<UUID> offeredBookIds, List<UUID> requestedBookIds,
            String offerType) {
        Offer offer;

        switch (offerType.toUpperCase()) {
            case "DONATION":
                DonationOffer donationOffer = new DonationOffer();
                donationOffer.setDonationMessage("Thank you for accepting my donation!");
                donationOffer.setIsCharity(false);
                donationOffer.setPickupRequired(false);
                offer = donationOffer;
                break;
            case "LOAN":
                LoanOffer loanOffer = new LoanOffer();
                loanOffer.setLoanDurationDays(30);
                loanOffer.setDepositRequired(false);
                offer = loanOffer;
                break;
            case "EXCHANGE":
            default:
                BookExchangeOffer exchangeOffer = new BookExchangeOffer();
                exchangeOffer.setIsNegotiable(true);
                exchangeOffer.setExchangeNotes("Standard book exchange");
                offer = exchangeOffer;
                break;
        }

        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setStatus("PENDING");
        offer = offerRepository.save(offer);

        notificationService.sendOfferCreatedNotification(receiver.getEmail(), sender.getEmail());

        saveBooksForOffer(offer, offeredBookIds, true);
        saveBooksForOffer(offer, requestedBookIds, false);

        return toDto(offer);
    }

    public List<OfferDto> getOffersReceivedByName(String username) {
        User user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Offer> offers = offerRepository.findAllWithDetails();
        List<OfferDto> result = new ArrayList<>();

        for (Offer offer : offers) {
            if (offer.getReceiver().getId().equals(user.getId())) {
                result.add(toDto(offer));
            }
        }

        return result;
    }

    private OfferDto deleteBooksFromOffer(Offer offer) {
        OfferDto offerDto = toDto(offer);
        // Set<OfferedBook> offeredsBooks = offer.getOfferedBooks();
        // Set<OfferedBook> requestedBooks = offer.getRequestedBooks();
        // for (OfferedBook offered : offeredsBooks) {
        // Book book = offered.getBook();
        // bookServie.deleteBookByTitleAndOwner(book.getTitle(),
        // book.getOwner().getEmail());
        // }
        //
        // for (OfferedBook requested : requestedBooks) {
        // Book book = requested.getBook();
        // bookServie.deleteBookByTitleAndOwner(book.getTitle(),
        // book.getOwner().getEmail());
        // }imi
        for (OfferedBook ob : offer.getOfferedBooks()) {
            Book book = ob.getBook();
            bookServie.deleteBookByTitleAndOwner(book.getTitle(), book.getOwner().getEmail());
        }
        return offerDto;
    }

    public OfferDto respondToOffer(UUID offerId, String newStatus, String username) {
        Offer offer = offerRepository.findByIdFull(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        String senderUsername = offer.getSender().getEmail();
        String receiverUsername = offer.getReceiver().getEmail();

        if (!username.equals(receiverUsername) && !username.equals(senderUsername)) {
            throw new RuntimeException("You are not authorized to respond to this offer.");
        }

        offer.setStatus(newStatus);
        offerRepository.save(offer);
        if (username.equals(receiverUsername)) {
            if ("ACCEPTED".equalsIgnoreCase(newStatus) || "OK".equalsIgnoreCase(newStatus)) {
                notificationService.sendOfferAcceptedNotification(offer.getSender().getEmail(), username);
                return deleteBooksFromOffer(offer);
            } else if ("REJECTED".equalsIgnoreCase(newStatus) || "NO".equalsIgnoreCase(newStatus)) {
                notificationService.sendOfferRejectedNotification(offer.getSender().getEmail(), username);
                return deleteBooksFromOffer(offer);
            }
        }
        if (username.equals(senderUsername)) {
            if ("CANCEL".equalsIgnoreCase(newStatus)) {
                return deleteBooksFromOffer(offer);
            }
        } else {
            throw new RuntimeException("You are not authorized to respond to this offer.");
        }
        return toDto(offer);
    }

    private List<UUID> resolveBookIdsFromTitles(List<String> titles, UUID ownerId) {
        return titles.stream()
                .flatMap(t -> Arrays.stream(t.split(",")))
                .map(String::trim)
                .map(title -> bookRepository.getBooksByTitle(title)
                        .orElseThrow(() -> new RuntimeException("Book not found: " + title)))
                .flatMap(List::stream)
                .filter(book -> book.getOwner() != null && book.getOwner().getId().equals(ownerId))
                .map(Book::getId)
                .toList();
    }

    private void saveBooksForOffer(Offer offer, List<UUID> bookIds, boolean isRequested) {
        for (UUID bookId : bookIds) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            OfferedBook offerBook = new OfferedBook();
            offerBook.setOffer(offer);
            offerBook.setBook(book);
            offerBook.setRequested(!isRequested);

            offeredBookRepository.save(offerBook);
        }
    }

    private OfferDto toDto(Offer offer) {
        OfferDto dto = new OfferDto();
        dto.setId(offer.getId());
        dto.setSenderEmail(offer.getSender().getEmail());
        dto.setReceiverEmail(offer.getReceiver().getEmail());
        dto.setStatus(offer.getStatus());

        List<String> offered = offer.getOfferedBooks().stream()
                .filter(b -> !b.isRequested())
                .map(ob -> ob.getBook().getTitle())
                .toList();

        List<String> requested = offer.getOfferedBooks().stream()
                .filter(OfferedBook::isRequested)
                .map(rb -> rb.getBook().getTitle())
                .toList();

        dto.setOfferedBookTitles(offered);
        dto.setRequestedBookTitles(requested);

        if (offer instanceof BookExchangeOffer) {
            dto.setOfferType("EXCHANGE");
        } else if (offer instanceof DonationOffer) {
            dto.setOfferType("DONATION");
        } else if (offer instanceof LoanOffer) {
            dto.setOfferType("LOAN");
        }

        return dto;
    }
}
