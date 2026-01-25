package app.controller;

import app.entity.Book;
import app.service.BookServie;
import app.service.dto.BookDto;
import app.service.dto.BookWithOwnerDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController implements SecuredRestController {

    private final BookServie bookServie;

    public BookController(BookServie bookServie) {
        this.bookServie = bookServie;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookWithOwnerDto>> getAllBooks() {
        List<BookWithOwnerDto> bookDtoList = bookServie.getAllBooks();
        return ResponseEntity.status(200).body(bookDtoList);
    }

    @GetMapping("/getByAuthor")
    public ResponseEntity<List<BookWithOwnerDto>> getBooksByAuthor(String author) {
        List<BookWithOwnerDto> bookDtoList = bookServie.getBooksByAuthor(author);
        if (bookDtoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.status(200).body(bookDtoList);
    }

    @PostMapping("/addBook")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Book> addBook(@RequestBody BookDto bookDto) {
        Book book = bookServie.addBook(bookDto);
        return ResponseEntity.status(201).body(book);
    }

    @DeleteMapping("/deleteByTitle")
    @PreAuthorize("#principal.name == @bookServie.getBookOwnerEmail(#title) or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteBookByTitle(@RequestParam String title,
            Principal principal) {
        boolean deleted = bookServie.deleteBookByTitleAndOwner(title, principal.getName());
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
    }

}
