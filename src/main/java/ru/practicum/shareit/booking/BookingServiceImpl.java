package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public SavedBookingDto getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        User booker = booking.getBooker();
        Item item = booking.getItem();
        if (booker.getId() != userId && item.getOwner().getId() != userId) {
            throw new BookingNotBelongException("Пользователь не имеет права на бронирование");
        }
        SavedBookingDto bookingToReturn = BookingMapper.toSavedBookingDto(booking);
        bookingToReturn.setItem(ItemMapper.toItemBookingDto(item));
        bookingToReturn.setBooker(UserMapper.toUserBookingDto(booker));

        return bookingToReturn;
    }

    @Override
    public List<SavedBookingDto> getBookingsOfUser(long from, long size, String state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<Booking> bookings;
        Pageable pageable = PageRequest.of((int) (from / size), (int) size);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(pageable, userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsOfUser(pageable, userId,
                        List.of(BookingStatus.APPROVED, BookingStatus.REJECTED), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookingsOfUser(pageable, userId,
                        LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookingsOfUser(pageable, userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findWaitingOrRejectedBookingsOfUser(pageable, userId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findWaitingOrRejectedBookingsOfUser(pageable, userId, BookingStatus.REJECTED);
                break;
            default:
                throw new NoSuchStateForBookingSearchException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toSavedBookingDtoList(bookings);
    }

    @Override
    public List<SavedBookingDto> getBookingsOfItemsOwner(long from, long size, String state, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<Booking> bookings;
        List<Long> itemsIds = bookingRepository.getItemsIdsOfOwner(userId);
        if (itemsIds.isEmpty()) {
            return null;
        }

        Pageable pageable = PageRequest.of((int) (from / size), (int) size);

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemsIds(pageable, itemsIds);
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentBookingsOfItemsOwner(pageable, itemsIds,
                        List.of(BookingStatus.APPROVED, BookingStatus.REJECTED), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findPastBookingsOfItemsOwner(pageable, itemsIds, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureBookingsOfItemsOwner(pageable, itemsIds, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findWaitingOrBookingsOfItemsOwner(pageable, itemsIds, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findWaitingOrBookingsOfItemsOwner(pageable, itemsIds, BookingStatus.REJECTED);
                break;
            default:
                throw new NoSuchStateForBookingSearchException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.toSavedBookingDtoList(bookings);
    }

    @Override
    @Transactional
    public SavedBookingDto saveNewBooking(BookingDto bookingDto, long userId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new InvalidDateException("Некорректные даты начала/конца бронирования");
        }

        Booking bookingToSave = BookingMapper.toBooking(bookingDto);
        bookingToSave.setItem(itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена")));
        if (!bookingToSave.getItem().getAvailable()) {
            throw new ItemNotAvailableException("Вещь недоступна для бронирования");
        }
        if (bookingToSave.getItem().getOwner().getId() == userId) {
            throw new ItemNotFoundException("Нельзя взять вещь у самого себя");
        }
        bookingToSave.setBooker(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
        bookingToSave.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(bookingToSave);
        User booker = savedBooking.getBooker();
        Item item = savedBooking.getItem();
        SavedBookingDto bookingToReturn = BookingMapper.toSavedBookingDto(savedBooking);
        bookingToReturn.setBooker(UserMapper.toUserBookingDto(booker));
        bookingToReturn.setItem(ItemMapper.toItemBookingDto(item));

        return bookingToReturn;
    }

    @Override
    @Transactional
    public SavedBookingDto updateBooking(long bookingId, long userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронирование не найдено"));
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (item.getOwner().getId() != userId) {
            throw new BookingNotBelongException("Пользователь не имеет права на бронирование");
        }
        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BookingAlreadyApprovedException("Бронирование уже подтверждено");
        }
        if (isApproved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking savedBooking = bookingRepository.save(booking);
        SavedBookingDto bookingToReturn = BookingMapper.toSavedBookingDto(savedBooking);
        bookingToReturn.setBooker(UserMapper.toUserBookingDto(booker));
        bookingToReturn.setItem(ItemMapper.toItemBookingDto(item));

        return bookingToReturn;
    }
}