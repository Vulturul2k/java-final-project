package app.service;

import app.entity.Book;
import app.entity.Offer;
import app.entity.User;
import app.repository.*;
import app.service.dto.BookDto;
import app.service.dto.BookWithOwnerDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookServie {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Value("${app.features.offers.maxPerUser:10}")
    private int maxOffersPerUser;

    @Autowired
    private OfferedBookRepository offeredBookRepository;

    @Autowired
    private OfferRepository offerRepository;

    public BookServie(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<BookWithOwnerDto> getBooksByAuthor(String author) {
        List<Book> books = bookRepository.getBooksByAuthor(author)
                .orElse(Collections.emptyList());

        return books.stream()
                .map(book -> new BookWithOwnerDto()
                        .setAuthor(book.getAuthor())
                        .setTitle(book.getTitle())
                        .setOwnerEmail(book.getOwner() != null ? book.getOwner().getEmail() : null)
                        .setOwnerUsername(book.getOwner() != null ? book.getOwner().getUsername() : null))
                .collect(Collectors.toList());
    }

    public List<BookWithOwnerDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();

        return books.stream()
                .map(book -> new BookWithOwnerDto()
                        .setAuthor(book.getAuthor())
                        .setTitle(book.getTitle())
                        .setOwnerEmail(book.getOwner() != null ? book.getOwner().getEmail() : null)
                        .setOwnerUsername(book.getOwner() != null ? book.getOwner().getUsername() : null))
                .collect(Collectors.toList());
    }

    public Book addBook(BookDto bookDto) {
        String email = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()).getUsername();

        User owner = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        int currentBookCount = getBookCountByOwner(email);
        if (currentBookCount >= maxOffersPerUser) {
            throw new RuntimeException(
                    "Maximum number of books reached. You can have maximum " + maxOffersPerUser + " books.");
        }

        Book book = new Book();
        book.setAuthor(bookDto.getAuthor());
        book.setTitle(bookDto.getTitle());
        book.setOwner(owner);

        return bookRepository.save(book);
    }

    public boolean deleteBookByTitleAndOwner(String title, String email) {
        Optional<List<Book>> optionalBooks = bookRepository.getBooksByTitle(title);
        if (optionalBooks.isPresent()) {
            for (Book book : optionalBooks.get()) {
                if (book.getOwner() != null && book.getOwner().getEmail().equals(email)) {

                    List<Offer> offers = offerRepository.findAllByBook(book.getId());

                    for (Offer offer : offers) {
                        offeredBookRepository.deleteAll(offer.getOfferedBooks());
                        // requestedBookRepository.deleteAll(offer.getRequestedBooks());
                        offer.setOfferedBooks(null);
                        offer.setRequestedBooks(null);
                        offerRepository.delete(offer);
                    }
                    bookRepository.delete(book);

                    return true;
                }
            }
        }
        return false;
    }

    public String getBookOwnerEmail(String title) {
        Optional<List<Book>> optionalBooks = bookRepository.getBooksByTitle(title);
        if (optionalBooks.isPresent() && !optionalBooks.get().isEmpty()) {
            Book book = optionalBooks.get().get(0);
            return book.getOwner() != null ? book.getOwner().getEmail() : null;
        }
        return null;
    }

    public int getBookCountByOwner(String email) {
        User owner = userRepository.findUserByEmail(email).orElse(null);
        if (owner == null) {
            return 0;
        }
        return bookRepository.findAll().stream()
                .filter(book -> book.getOwner() != null && book.getOwner().getId().equals(owner.getId()))
                .toList()
                .size();
    }
}
