package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    Item item;
    Item item2;
    User user;
    User user2;
    Booking booking;
    Booking booking2;
    Booking booking3;
    Booking booking4;
    Booking booking5;
    Booking booking6;

    @BeforeAll
    void beforeAll() {
        user = User.builder().name("user").email("user@mail.com").build();
        user2 = User.builder().name("user2").email("user2@mail.com").build();
        userRepository.save(user);
        userRepository.save(user2);
        item = Item.builder().name("item").description("item Description").available(true).owner(user)
                .build();
        item2 = Item.builder().name("item2").description("item2 Description").available(true).owner(user2)
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);
        booking = Booking.builder().booker(user).item(item2)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED).build();
        booking2 = Booking.builder().booker(user).item(item2)
                .start(LocalDateTime.now().plusYears(1))
                .end(LocalDateTime.now().plusYears(1).plusDays(1))
                .status(BookingStatus.APPROVED).build();
        booking3 = Booking.builder().booker(user).item(item2)
                .start(LocalDateTime.now().minusMonths(1).minusDays(1))
                .end(LocalDateTime.now().minusMonths(1))
                .status(BookingStatus.APPROVED).build();
        booking4 = Booking.builder().booker(user).item(item2)
                .start(LocalDateTime.now().minusMonths(2).minusDays(2))
                .end(LocalDateTime.now().minusMonths(2))
                .status(BookingStatus.REJECTED).build();
        booking5 = Booking.builder().booker(user2).item(item)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED).build();
        booking6 = Booking.builder().booker(user2).item(item)
                .start(LocalDateTime.now().minusMonths(1).minusDays(1))
                .end(LocalDateTime.now().minusMonths(1))
                .status(BookingStatus.APPROVED).build();
        bookingRepository.save(booking);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        bookingRepository.save(booking5);
        bookingRepository.save(booking6);
    }

    @Test
    void findAllByBookerIdOrderByEndDesc() {
        List<Booking> result = bookingRepository.findAllByBookerIdOrderByEndDesc(Pageable.unpaged(), user.getId());

        assertEquals(4, result.size());
        assertEquals(booking2.getId(), result.get(0).getId());
        assertEquals(booking.getId(), result.get(1).getId());
        assertEquals(booking3.getId(), result.get(2).getId());
        assertEquals(booking4.getId(), result.get(3).getId());
    }

    @Test
    void findCurrentBookingsOfUser() {
        List<Booking> result = bookingRepository.findCurrentBookingsOfUser(Pageable.unpaged(), user.getId(),
                List.of(BookingStatus.APPROVED), LocalDateTime.now());

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findPastBookingsOfUser() {
        List<Booking> result = bookingRepository.findPastBookingsOfUser(Pageable.unpaged(), user.getId(),
                LocalDateTime.now());

        assertEquals(2, result.size());
        assertEquals(booking3.getId(), result.get(0).getId());
        assertEquals(booking4.getId(), result.get(1).getId());
    }

    @Test
    void findFutureBookingsOfUser() {
        List<Booking> result = bookingRepository.findFutureBookingsOfUser(Pageable.unpaged(), user.getId(),
                LocalDateTime.now());

        assertEquals(1, result.size());
        assertEquals(booking2.getId(), result.get(0).getId());
    }

    @Test
    void findWaitingOrRejectedBookingsOfUser() {
        List<Booking> result = bookingRepository.findWaitingOrRejectedBookingsOfUser(Pageable.unpaged(),
                user.getId(), BookingStatus.REJECTED);

        assertEquals(1, result.size());
        assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    void getItemsIdsOfOwner() {
        List<Long> result = bookingRepository.getItemsIdsOfOwner(user2.getId());

        assertEquals(1, result.size());
        assertEquals(item2.getId(), result.get(0));
    }

    @Test
    void findAllByItemsIds() {
        List<Booking> result = bookingRepository.findAllByItemsIds(Pageable.unpaged(),
                List.of(item2.getId()));

        assertEquals(4, result.size());
        assertEquals(booking2.getId(), result.get(0).getId());
        assertEquals(booking.getId(), result.get(1).getId());
        assertEquals(booking3.getId(), result.get(2).getId());
        assertEquals(booking4.getId(), result.get(3).getId());
    }

    @Test
    void findCurrentBookingsOfItemsOwner() {
        List<Booking> result = bookingRepository.findCurrentBookingsOfItemsOwner(Pageable.unpaged(),
                List.of(item2.getId()), List.of(BookingStatus.APPROVED), LocalDateTime.now());

        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void findPastBookingsOfItemsOwner() {
        List<Booking> result = bookingRepository.findPastBookingsOfItemsOwner(Pageable.unpaged(),
                List.of(item2.getId()), LocalDateTime.now());

        assertEquals(2, result.size());
        assertEquals(booking3.getId(), result.get(0).getId());
        assertEquals(booking4.getId(), result.get(1).getId());
    }

    @Test
    void findFutureBookingsOfItemsOwner() {
        List<Booking> result = bookingRepository.findFutureBookingsOfItemsOwner(Pageable.unpaged(),
                List.of(item2.getId()), LocalDateTime.now());

        assertEquals(1, result.size());
        assertEquals(booking2.getId(), result.get(0).getId());
    }

    @Test
    void findWaitingOrBookingsOfItemsOwner() {
        List<Booking> result = bookingRepository.findWaitingOrBookingsOfItemsOwner(Pageable.unpaged(),
                List.of(item2.getId()), BookingStatus.REJECTED);

        assertEquals(1, result.size());
        assertEquals(booking4.getId(), result.get(0).getId());
    }

    @Test
    void findApprovedItemsBookings() {
        List<Booking> result = bookingRepository.findApprovedItemsBookings(List.of(item2.getId()));

        assertEquals(3, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
        assertEquals(booking2.getId(), result.get(1).getId());
        assertEquals(booking3.getId(), result.get(2).getId());
    }

    @Test
    void findBookingByItemIdAndBookerId() {
        List<Booking> result = bookingRepository.findBookingByItemIdAndBookerId(item2.getId(), user.getId(),
                Pageable.unpaged());

        assertEquals(3, result.size());
        assertEquals(booking3.getId(), result.get(0).getId());
        assertEquals(booking.getId(), result.get(1).getId());
        assertEquals(booking2.getId(), result.get(2).getId());
    }

    @AfterAll
    void afterAll() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE bookings ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE items ALTER COLUMN ID RESTART WITH 1;");
    }
}
