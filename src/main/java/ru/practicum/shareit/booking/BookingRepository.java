package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByEndDesc(Pageable pageable, long bookerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = ?1 AND b.status in (?2) " +
            "AND b.start < ?3 AND b.end > ?3 ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsOfUser(Pageable pageable, long bookerId, List<BookingStatus> status, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = ?1 " +
            "AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastBookingsOfUser(Pageable pageable, long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = ?1 " +
            "AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureBookingsOfUser(Pageable pageable, long bookerId, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = ?1 AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingOrRejectedBookingsOfUser(Pageable pageable, long bookerId, BookingStatus status);

    @Query("SELECT i.id FROM Item i WHERE i.owner.id = ?1")
    List<Long> getItemsIdsOfOwner(long ownerId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemsIds(Pageable pageable, List<Long> itemsIds);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) AND b.status in (?2) " +
            "AND b.start < ?3 AND b.end > ?3 ORDER BY b.start DESC")
    List<Booking> findCurrentBookingsOfItemsOwner(Pageable pageable, List<Long> itemsIds, List<BookingStatus> status, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) " +
            "AND b.end < ?2 ORDER BY b.start DESC")
    List<Booking> findPastBookingsOfItemsOwner(Pageable pageable, List<Long> itemsIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) " +
            "AND b.start > ?2 ORDER BY b.start DESC")
    List<Booking> findFutureBookingsOfItemsOwner(Pageable pageable, List<Long> itemsIds, LocalDateTime now);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findWaitingOrRejectedBookingsOfItemsOwner(Pageable pageable, List<Long> itemsIds, BookingStatus status);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id in (?1) " +
            "AND b.status = 'APPROVED'")
    List<Booking> findApprovedItemsBookings(List<Long> itemsIds);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.item.id = ?1 AND b.booker.id = ?2 " +
            "AND b.status = 'APPROVED' ORDER BY b.start ASC")
    List<Booking> findBookingByItemIdAndBookerId(long itemId, long bookerId, Pageable pageable);
}
