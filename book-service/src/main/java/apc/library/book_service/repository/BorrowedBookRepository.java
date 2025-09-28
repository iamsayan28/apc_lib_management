package apc.library.book_service.repository;

import apc.library.book_service.model.BorrowedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBook, Long> {
    Optional<BorrowedBook> findByBookIdAndUserEmail(Long bookId, String userEmail);
}