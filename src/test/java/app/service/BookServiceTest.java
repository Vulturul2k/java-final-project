package app.service;

import app.entity.Book;
import app.entity.User;
import app.repository.BookRepository;
import app.repository.OfferRepository;
import app.repository.OfferedBookRepository;
import app.repository.UserRepository;
import app.service.dto.BookDto;
import app.service.dto.BookWithOwnerDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferedBookRepository offeredBookRepository;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private BookServie bookService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;

    @BeforeEach
    void setUp() {
        // Set default value for maxOffersPerUser since it's injected via @Value
        ReflectionTestUtils.setField(bookService, "maxOffersPerUser", 10);
        // Manually inject autowired fields not in constructor
        ReflectionTestUtils.setField(bookService, "offerRepository", offerRepository);
        ReflectionTestUtils.setField(bookService, "offeredBookRepository", offeredBookRepository);
    }

    @Test
    void getBooksByAuthor_ShouldReturnList() {
        // Arrange
        String author = "Author Name";
        Book book = new Book();
        book.setAuthor(author);
        book.setTitle("Title");

        when(bookRepository.getBooksByAuthor(author)).thenReturn(Optional.of(List.of(book)));

        // Act
        List<BookWithOwnerDto> result = bookService.getBooksByAuthor(author);

        // Assert
        assertEquals(1, result.size());
        assertEquals(author, result.get(0).getAuthor());
    }

    @Test
    void addBook_ShouldSaveBook_WhenLimitNotReached() {
        // Arrange
        BookDto bookDto = new BookDto();
        bookDto.setAuthor("Author");
        bookDto.setTitle("Title");

        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setId(UUID.randomUUID());

        // Mock Static SecurityContext
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);

        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        // Mock getBookCountByOwner logic (returns 0 by default if findAll mock returns
        // empty)
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());
        when(bookRepository.save(any(Book.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Book savedBook = bookService.addBook(bookDto);

        // Assert
        assertNotNull(savedBook);
        assertEquals("Title", savedBook.getTitle());

        mockedSecurityContextHolder.close();
    }

    @Test
    void deleteBookByTitleAndOwner_ShouldDelete_WhenOwnerMatches() {
        // Arrange
        String title = "Title";
        String email = "owner@example.com";

        User owner = new User();
        owner.setEmail(email);

        Book book = new Book();
        book.setId(UUID.randomUUID());
        book.setTitle(title);
        book.setOwner(owner);

        when(bookRepository.getBooksByTitle(title)).thenReturn(Optional.of(List.of(book)));
        when(offerRepository.findAllByBook(book.getId())).thenReturn(Collections.emptyList());

        // Act
        boolean result = bookService.deleteBookByTitleAndOwner(title, email);

        // Assert
        assertTrue(result);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void getAllBooks_ShouldReturnAllBooks() {
        Book book = new Book();
        book.setTitle("T");
        book.setAuthor("A");

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookWithOwnerDto> result = bookService.getAllBooks();

        assertEquals(1, result.size());
    }

    @Test
    void getBookOwnerEmail_ShouldReturnEmail() {
        String email = "owner@test.com";
        User owner = new User();
        owner.setEmail(email);
        Book book = new Book();
        book.setOwner(owner);

        when(bookRepository.getBooksByTitle("Title")).thenReturn(Optional.of(List.of(book)));

        String result = bookService.getBookOwnerEmail("Title");

        assertEquals(email, result);
    }

    @Test
    void addBook_ShouldThrowException_WhenLimitReached() {
        BookDto dto = new BookDto();
        dto.setTitle("T");
        dto.setAuthor("A");
        String email = "u@e.com";
        User user = new User();
        user.setEmail(email);
        user.setId(UUID.randomUUID());

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);

        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(email);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        // Mock finding 11 books
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            Book b = new Book();
            b.setOwner(user);
            books.add(b);
        }
        when(bookRepository.findAll()).thenReturn(books);

        assertThrows(RuntimeException.class, () -> bookService.addBook(dto));

        mockedSecurityContextHolder.close();
    }
}
