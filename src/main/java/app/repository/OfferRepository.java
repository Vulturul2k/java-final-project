package app.repository;

import app.entity.Offer;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    @Query("SELECT o FROM Offer o " +
            "JOIN FETCH o.sender " +
            "JOIN FETCH o.receiver " +
            "LEFT JOIN FETCH o.offeredBooks ob " +
            "LEFT JOIN FETCH ob.book " +
            "LEFT JOIN FETCH o.requestedBooks rb " +
            "LEFT JOIN FETCH rb.book")
    List<Offer> findAllWithDetails();

    @EntityGraph(attributePaths = { "offeredBooks.book", "requestedBooks.book" })
    List<Offer> findAll();

    @Query("""
                SELECT o FROM Offer o
                JOIN FETCH o.sender
                JOIN FETCH o.receiver
                LEFT JOIN FETCH o.offeredBooks ob
                LEFT JOIN FETCH ob.book
                LEFT JOIN FETCH o.requestedBooks rb
                LEFT JOIN FETCH rb.book
                WHERE o.id = :id
            """)
    Optional<Offer> findByIdFull(@Param("id") UUID id);

    @Query("SELECT o FROM Offer o JOIN o.offeredBooks ob WHERE ob.book.id = :bookId OR o IN (SELECT o2 FROM Offer o2 JOIN o2.requestedBooks rb WHERE rb.book.id = :bookId)")
    List<Offer> findAllByBook(@Param("bookId") UUID bookId);

}
