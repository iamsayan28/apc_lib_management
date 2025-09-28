// package apc.library.book_service.service;

// import apc.library.book_service.model.Book;
// import apc.library.book_service.repository.BookRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;

// @Service
// public class BookService {

//     @Autowired
//     private BookRepository bookRepository;

//     public List<Book> findAllBooks() {
//         return bookRepository.findAll();
//     }

//     public Optional<Book> findBookById(Long id) {
//         return bookRepository.findById(id);
//     }

//     public Book saveBook(Book book) {
//         return bookRepository.save(book);
//     }

//     public Book updateBook(Long id, Book bookDetails) {
//         Book book = bookRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

//         book.setTitle(bookDetails.getTitle());
//         book.setAuthor(bookDetails.getAuthor());
//         book.setAvailableCopies(bookDetails.getAvailableCopies());
//         return bookRepository.save(book);
//     }

//     public void deleteBook(Long id) {
//         bookRepository.deleteById(id);
//     }
    
//     // --- New Business Logic for Borrowing/Returning ---

//     public Book borrowBook(Long bookId) {
//         Book book = bookRepository.findById(bookId)
//                 .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
//         if (book.getAvailableCopies() <= 0) {
//             throw new RuntimeException("No available copies for book: " + book.getTitle());
//         }
        
//         book.setAvailableCopies(book.getAvailableCopies() - 1);
//         return bookRepository.save(book);
//     }

//     public Book returnBook(Long bookId) {
//         Book book = bookRepository.findById(bookId)
//                 .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
//         book.setAvailableCopies(book.getAvailableCopies() + 1);
//         return bookRepository.save(book);
//     }
// }

package apc.library.book_service.service;

import apc.library.book_service.model.Book;
import apc.library.book_service.model.BorrowedBook;
import apc.library.book_service.repository.BookRepository;
import apc.library.book_service.repository.BorrowedBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BorrowedBookRepository borrowedBookRepository;

    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(Book book) {
        if (bookRepository.findByTitle(book.getTitle()).isPresent()) {
            throw new RuntimeException("Book with title '" + book.getTitle() + "' already exists.");
        }
        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setAvailableCopies(bookDetails.getAvailableCopies());
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
    
    // --- New Business Logic for Borrowing/Returning ---

    public Book borrowBook(Long bookId, String userEmail) {
        if (borrowedBookRepository.findByBookIdAndUserEmail(bookId, userEmail).isPresent()) {
            throw new RuntimeException("You have already borrowed this book.");
        }

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies for book: " + book.getTitle());
        }
        
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        borrowedBookRepository.save(new BorrowedBook(bookId, userEmail));
        return bookRepository.save(book);
    }

    public Book returnBook(Long bookId, String userEmail) {
        BorrowedBook borrowedBook = borrowedBookRepository.findByBookIdAndUserEmail(bookId, userEmail)
                .orElseThrow(() -> new RuntimeException("You have not borrowed this book."));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookId));
        
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        borrowedBookRepository.delete(borrowedBook);
        return bookRepository.save(book);
    }
}