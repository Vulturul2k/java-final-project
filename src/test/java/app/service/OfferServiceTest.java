package app.service;

import app.entity.*;
import app.repository.BookRepository;
import app.repository.OfferRepository;
import app.repository.OfferedBookRepository;
import app.repository.UserRepository;
import app.service.dto.CreateOfferFromContextDto;
import app.service.dto.OfferDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private OfferedBookRepository offeredBookRepository;
    @Mock
    private BookServie bookServie;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OfferService offerService;

    @Test
    void createFromAuthenticatedUser_ShouldCreateOffer() {
        // Arrange
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";
        String bookTitle = "Book1";

        CreateOfferFromContextDto dto = new CreateOfferFromContextDto();
        dto.setReceiverEmail(receiverEmail);
        dto.setOfferedBookTitles(List.of(bookTitle));
        dto.setRequestedBookTitles(Collections.emptyList());
        dto.setOfferType("EXCHANGE");

        User sender = new User();
        sender.setId(UUID.randomUUID());
        sender.setEmail(senderEmail);

        User receiver = new User();
        receiver.setId(UUID.randomUUID());
        receiver.setEmail(receiverEmail);

        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle(bookTitle);
        book.setOwner(sender);

        when(userRepository.findUserByEmail(senderEmail)).thenReturn(Optional.of(sender));
        when(userRepository.findUserByEmail(receiverEmail)).thenReturn(Optional.of(receiver));
        when(bookRepository.getBooksByTitle(bookTitle)).thenReturn(Optional.of(List.of(book)));
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> {
            Offer o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        // Act
        OfferDto result = offerService.createFromAuthenticatedUser(dto, senderEmail);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(notificationService).sendOfferCreatedNotification(receiverEmail, senderEmail);
    }

    @Test
    void createOffer_ShouldCreateDonationOffer() {
        // Arrange
        User sender = new User();
        sender.setEmail("s@e.com");
        sender.setId(UUID.randomUUID());
        User receiver = new User();
        receiver.setEmail("r@e.com");
        receiver.setId(UUID.randomUUID());

        when(offerRepository.save(any(DonationOffer.class))).thenAnswer(i -> {
            DonationOffer o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        // Act
        OfferDto result = offerService.createOffer(sender, receiver, List.of(), List.of(), "DONATION");

        // Assert
        assertEquals("DONATION", result.getOfferType());
    }

    @Test
    void createOffer_ShouldCreateLoanOffer() {
        // Arrange
        User sender = new User();
        sender.setEmail("s@e.com");
        sender.setId(UUID.randomUUID());
        User receiver = new User();
        receiver.setEmail("r@e.com");
        receiver.setId(UUID.randomUUID());

        when(offerRepository.save(any(LoanOffer.class))).thenAnswer(i -> {
            LoanOffer o = i.getArgument(0);
            o.setId(UUID.randomUUID());
            return o;
        });

        // Act
        OfferDto result = offerService.createOffer(sender, receiver, List.of(), List.of(), "LOAN");

        // Assert
        assertEquals("LOAN", result.getOfferType());
    }

    @Test
    void getOffersReceivedByName_ShouldReturnOffers() {
        // Arrange
        String email = "me@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(UUID.randomUUID());

        User sender = new User();
        sender.setEmail("sender@example.com");
        sender.setId(UUID.randomUUID());

        Offer offer = new BookExchangeOffer();
        offer.setId(UUID.randomUUID());
        offer.setReceiver(user);
        offer.setSender(sender);
        offer.setStatus("PENDING");

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(offerRepository.findAllWithDetails()).thenReturn(List.of(offer));

        // Act
        List<OfferDto> result = offerService.getOffersReceivedByName(email);

        // Assert
        assertEquals(1, result.size());
        assertEquals(offer.getId(), result.get(0).getId());
    }

    @Test
    void respondToOffer_ShouldUpdateStatus_WhenAccepted() {
        // Arrange
        UUID offerId = UUID.randomUUID();
        String receiverEmail = "receiver@example.com";
        String senderEmail = "sender@example.com";

        User receiver = new User();
        receiver.setEmail(receiverEmail);
        receiver.setId(UUID.randomUUID());

        User sender = new User();
        sender.setEmail(senderEmail);
        sender.setId(UUID.randomUUID());

        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setReceiver(receiver);
        offer.setSender(sender);
        offer.setStatus("PENDING");

        when(offerRepository.findByIdFull(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        OfferDto result = offerService.respondToOffer(offerId, "ACCEPTED", receiverEmail);

        // Assert
        assertEquals("ACCEPTED", result.getStatus());
        verify(notificationService).sendOfferAcceptedNotification(senderEmail, receiverEmail);
    }

    @Test
    void respondToOffer_ShouldUpdateStatus_WhenRejected() {
        UUID offerId = UUID.randomUUID();
        String receiverEmail = "receiver@example.com";
        String senderEmail = "sender@example.com";

        User receiver = new User();
        receiver.setEmail(receiverEmail);
        User sender = new User();
        sender.setEmail(senderEmail);

        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setReceiver(receiver);
        offer.setSender(sender);
        offer.setStatus("PENDING");

        when(offerRepository.findByIdFull(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));

        OfferDto result = offerService.respondToOffer(offerId, "REJECTED", receiverEmail);

        assertEquals("REJECTED", result.getStatus());
        verify(notificationService).sendOfferRejectedNotification(senderEmail, receiverEmail);
    }

    @Test
    void respondToOffer_ShouldCancel_WhenSenderCancels() {
        UUID offerId = UUID.randomUUID();
        String receiverEmail = "receiver@example.com";
        String senderEmail = "sender@example.com";

        User receiver = new User();
        receiver.setEmail(receiverEmail);
        User sender = new User();
        sender.setEmail(senderEmail);

        Offer offer = new Offer();
        offer.setId(offerId);
        offer.setReceiver(receiver);
        offer.setSender(sender);
        offer.setStatus("PENDING");

        when(offerRepository.findByIdFull(offerId)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(i -> i.getArgument(0));

        OfferDto result = offerService.respondToOffer(offerId, "CANCEL", senderEmail);

        assertEquals("CANCEL", result.getStatus());
    }

    @Test
    void respondToOffer_ShouldThrowException_WhenUnauthorized() {
        // Arrange
        UUID offerId = UUID.randomUUID();
        String otherEmail = "other@example.com";

        Offer offer = new Offer();
        offer.setSender(new User().setEmail("sender@example.com"));
        offer.setReceiver(new User().setEmail("receiver@example.com"));

        when(offerRepository.findByIdFull(offerId)).thenReturn(Optional.of(offer));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> offerService.respondToOffer(offerId, "ACCEPTED", otherEmail));
    }
}
