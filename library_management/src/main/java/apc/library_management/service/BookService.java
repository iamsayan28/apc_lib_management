package apc.library_management.service;

import apc.library_management.model.Book;
import apc.library_management.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public String borrowBook(Long bookId, Long userId) {
        return "User " + userId + " borrowed book " + bookId;
    }
    public Book updateBook(Long id, Book bookDetails) {
    Book book = bookRepository.findById(id).orElse(null);
    if (book != null) {
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setAvailableCopies(bookDetails.getAvailableCopies());
        return bookRepository.save(book);
    }
    return null;
}

}
