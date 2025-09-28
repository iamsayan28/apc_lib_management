// package apc.library.book_service.controller;

// import apc.library.book_service.model.Book;
// import apc.library.book_service.service.BookService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/books")
// public class BookController {

//     @Autowired
//     private BookService bookService;

//     // --- Public endpoints, accessible by any authenticated user ---

//     @GetMapping
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public List<Book> getAllBooks() {
//         return bookService.findAllBooks();
//     }

//     @GetMapping("/{id}")
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public ResponseEntity<Book> getBookById(@PathVariable Long id) {
//         return bookService.findBookById(id)
//                 .map(ResponseEntity::ok)
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     // --- Endpoints for borrowing and returning books ---

//     @PostMapping("/{id}/borrow")
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public ResponseEntity<Book> borrowBook(@PathVariable Long id) {
//         try {
//             return ResponseEntity.ok(bookService.borrowBook(id));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(null); // Or a proper error response
//         }
//     }

//     @PostMapping("/{id}/return")
//     @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
//     public ResponseEntity<Book> returnBook(@PathVariable Long id) {
//         try {
//             return ResponseEntity.ok(bookService.returnBook(id));
//         } catch (RuntimeException e) {
//             return ResponseEntity.badRequest().body(null); // Or a proper error response
//         }
//     }
    
//     // --- Admin-only endpoints ---

//     @PostMapping
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public Book createBook(@RequestBody Book book) {
//         return bookService.saveBook(book);
//     }

//     @PutMapping("/{id}")
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
//         return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
//     }

//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//     public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
//         bookService.deleteBook(id);
//         return ResponseEntity.noContent().build();
//     }
// }

package apc.library.book_service.controller;

import apc.library.book_service.model.Book;
import apc.library.book_service.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;


    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.findBookById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --- Endpoints for borrowing and returning books ---

    @PostMapping("/{id}/borrow")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> borrowBook(@PathVariable Long id, Principal principal) {
        try {
            return ResponseEntity.ok(bookService.borrowBook(id, principal.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> returnBook(@PathVariable Long id, Principal principal) {
        try {
            return ResponseEntity.ok(bookService.returnBook(id, principal.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // --- Admin-only endpoints ---

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> createBook(@RequestBody Book book) {
        try {
            return ResponseEntity.ok(bookService.saveBook(book));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        try {
            return ResponseEntity.ok(bookService.updateBook(id, bookDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok("Book deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}