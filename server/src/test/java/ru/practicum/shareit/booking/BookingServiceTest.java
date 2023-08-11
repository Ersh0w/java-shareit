package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserBookingDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    @InjectMocks
    private BookingServiceImpl bookingService;

    Item item;
    ItemBookingDto itemBookingDto;
    User user;
    User user2;
    UserBookingDto userBookingDto;
    ItemRequest itemRequest;
    PageRequest pageRequest = PageRequest.of(2, 5);
    Booking booking;
    BookingDto bookingDto;
    SavedBookingDto savedBookingDto;
    SavedBookingDto expectedBookingDto;
    long userId = 1;
    long user2Id = 2;
    long itemId = 1;
    long bookingId = 1;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).name("name").email("mail@mail.com").build();
        user2 = User.builder().id(user2Id).name("name2").email("mail2@mail.com").build();
        itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now().minusDays(1))
                .description("ItemRequest Description").requestor(user).build();
        item = Item.builder().id(itemId).available(true).description("item description").request(itemRequest)
                .owner(user).build();
        booking = Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(2))
                .item(item).booker(user2).status(BookingStatus.APPROVED).id(bookingId).build();
        bookingDto = BookingDto.builder().start(booking.getStart()).end(booking.getEnd())
                .itemId(booking.getItem().getId()).build();
        savedBookingDto = SavedBookingDto.builder().start(booking.getStart()).end(booking.getEnd())
                .id(bookingId).status(booking.getStatus()).build();
        userBookingDto = UserBookingDto.builder().id(user2Id).build();
        itemBookingDto = ItemBookingDto.builder().id(itemId).name(item.getName()).build();
        expectedBookingDto = savedBookingDto;
        expectedBookingDto.setBooker(userBookingDto);
        expectedBookingDto.setItem(itemBookingDto);
    }

    @Test
    void getBookingById_shouldBeFound() {
        try (MockedStatic<ItemMapper> mockStaticItem = mockStatic(ItemMapper.class);
             MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class);
             MockedStatic<UserMapper> mockStaticUser = mockStatic(UserMapper.class)) {
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            mockStaticItem.when(() -> ItemMapper.toItemBookingDto(item))
                    .thenReturn(itemBookingDto);
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDto(booking))
                    .thenReturn(savedBookingDto);
            mockStaticUser.when(() -> UserMapper.toUserBookingDto(user2))
                    .thenReturn(userBookingDto);

            SavedBookingDto savedBookingDtoActual = bookingService.getBookingById(bookingId, user2Id);

            assertEquals(expectedBookingDto, savedBookingDtoActual);
            assertEquals(expectedBookingDto.getBooker().getId(), savedBookingDtoActual.getBooker().getId());
            assertEquals(expectedBookingDto.getItem().getId(), savedBookingDtoActual.getItem().getId());
            verify(bookingRepository).findById(bookingId);
        }
    }

    @Test
    void getBookingById_shouldNotBeFound_fail() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingById_bookingNotBelongToUser_fail() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingNotBelongException.class, () -> bookingService.getBookingById(bookingId, userId + 2));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void getBookingsOfUser_shouldBeFound_case_ALL() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findAllByBookerIdOrderByEndDesc(pageRequest, userId)).thenReturn(List.of((booking)));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfUser(10, 5, "ALL", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findAllByBookerIdOrderByEndDesc(pageRequest, userId);
        }
    }

    @Test
    void getBookingsOfUser_shouldBeFound_case_CURRENT() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findCurrentBookingsOfUser(any(PageRequest.class), anyLong(), anyList(),
                    any(LocalDateTime.class))).thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfUser(10, 5, "CURRENT", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findCurrentBookingsOfUser(any(PageRequest.class), anyLong(),
                    anyList(), any(LocalDateTime.class));
        }
    }

    @Test
    void getBookingsOfUser_shouldBeFound_case_PAST() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findPastBookingsOfUser(any(PageRequest.class), anyLong(), any(LocalDateTime.class)))
                    .thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfUser(10, 5, "PAST", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findPastBookingsOfUser(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        }
    }

    @Test
    void getBookingsOfUser_shouldBeFound_case_FUTURE() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findFutureBookingsOfUser(any(PageRequest.class), anyLong(), any(LocalDateTime.class)))
                    .thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfUser(10, 5, "FUTURE", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findFutureBookingsOfUser(any(PageRequest.class), anyLong(), any(LocalDateTime.class));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "REJECTED"})
    void getBookingsOfUser_shouldBeFound_case_WAITING_OR_REJECTED(String status) {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findWaitingOrRejectedBookingsOfUser(any(PageRequest.class), anyLong(),
                    any(BookingStatus.class)))
                    .thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfUser(10, 5, status, userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findWaitingOrRejectedBookingsOfUser(any(PageRequest.class), anyLong(),
                    any(BookingStatus.class));
        }
    }

    @Test
    void getBookingsOfUser_unsupportedState_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NoSuchStateForBookingSearchException.class, () -> bookingService.getBookingsOfUser(10,
                5, "FUTUREPAST", userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getBookingsOfItemsOwner_shouldBeFound_case_ALL() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));
            when(bookingRepository.findAllByItemsIds(pageRequest, List.of(itemId))).thenReturn(List.of((booking)));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfItemsOwner(10, 5, "ALL", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).getItemsIdsOfOwner(userId);
            verify(bookingRepository).findAllByItemsIds(pageRequest, List.of(itemId));
        }
    }

    @Test
    void getBookingsOfItemsOwner_shouldBeFound_case_CURRENT() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));
            when(bookingRepository.findCurrentBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfItemsOwner(10, 5, "CURRENT", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).getItemsIdsOfOwner(userId);
            verify(bookingRepository).findCurrentBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    anyList(), any(LocalDateTime.class));
        }
    }

    @Test
    void getBookingsOfItemsOwner_shouldBeFound_case_PAST() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));
            when(bookingRepository.findPastBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(LocalDateTime.class))).thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfItemsOwner(10, 5, "PAST", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).getItemsIdsOfOwner(userId);
            verify(bookingRepository).findPastBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(LocalDateTime.class));
        }
    }

    @Test
    void getBookingsOfItemsOwner_shouldBeFound_case_FUTURE() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));
            when(bookingRepository.findFutureBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(LocalDateTime.class))).thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));

            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfItemsOwner(10, 5, "FUTURE", userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).getItemsIdsOfOwner(userId);
            verify(bookingRepository).findFutureBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(LocalDateTime.class));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"WAITING", "REJECTED"})
    void getBookingsOfItemsOwner_shouldBeFound_case_WAITING_or_REJECTED(String state) {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));
            when(bookingRepository.findWaitingOrRejectedBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(BookingStatus.class))).thenReturn(List.of(booking));
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDtoList(List.of(booking)))
                    .thenReturn(List.of(savedBookingDto));


            List<SavedBookingDto> savedBookingDtoListActual = bookingService
                    .getBookingsOfItemsOwner(10, 5, state, userId);

            assertEquals(List.of(savedBookingDto), savedBookingDtoListActual);
            verify(userRepository).findById(userId);
            verify(bookingRepository).getItemsIdsOfOwner(userId);
            verify(bookingRepository).findWaitingOrRejectedBookingsOfItemsOwner(any(PageRequest.class), anyList(),
                    any(BookingStatus.class));
        }
    }

    @Test
    void getBookingsOfItemsOwner_unsupportedState_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getItemsIdsOfOwner(userId)).thenReturn(List.of(itemId));

        assertThrows(NoSuchStateForBookingSearchException.class, () -> bookingService.getBookingsOfItemsOwner(10,
                5, "FUTUREPAST", userId));

        verify(userRepository).findById(userId);
        verify(bookingRepository).getItemsIdsOfOwner(userId);
    }

    @Test
    void saveNewBooking_shouldBeSaved() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class);
             MockedStatic<ItemMapper> mockStaticItem = mockStatic(ItemMapper.class);
             MockedStatic<UserMapper> mockStaticUser = mockStatic(UserMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBooking(bookingDto))
                    .thenReturn(booking);
            when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
            when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
            when(bookingRepository.save(booking)).thenReturn(booking);
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDto(booking))
                    .thenReturn(savedBookingDto);
            mockStaticUser.when(() -> UserMapper.toUserBookingDto(user2))
                    .thenReturn(userBookingDto);
            mockStaticItem.when(() -> ItemMapper.toItemBookingDto(item))
                    .thenReturn(itemBookingDto);

            SavedBookingDto savedBookingDtoActual = bookingService
                    .saveNewBooking(bookingDto, user2Id);

            assertEquals(savedBookingDto, savedBookingDtoActual);
            verify(itemRepository).findById(bookingDto.getItemId());
            verify(userRepository).findById(user2Id);
            verify(bookingRepository).save(booking);
        }
    }

    @Test
    void saveNewBooking_invalidDate_fail() {
        bookingDto.setEnd(LocalDateTime.now().minusDays(10));

        assertThrows(InvalidDateException.class, () -> bookingService.saveNewBooking(bookingDto, user2Id));
    }

    @Test
    void saveNewBooking_itemNotFound_fail() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBooking(bookingDto))
                    .thenReturn(booking);
            when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

            assertThrows(ItemNotFoundException.class, () -> bookingService.saveNewBooking(bookingDto, user2Id));

            verify(itemRepository).findById(bookingDto.getItemId());
        }
    }

    @Test
    void saveNewBooking_itemNotAvailableForBooking_fail() {
        item.setAvailable(false);
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBooking(bookingDto))
                    .thenReturn(booking);
            when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

            assertThrows(ItemNotAvailableException.class, () -> bookingService.saveNewBooking(bookingDto, user2Id));

            verify(itemRepository).findById(bookingDto.getItemId());
        }
    }

    @Test
    void saveNewBooking_ownerTriesBookFromHimself_fail() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBooking(bookingDto))
                    .thenReturn(booking);
            when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

            assertThrows(ItemNotFoundException.class, () -> bookingService.saveNewBooking(bookingDto, userId));

            verify(itemRepository).findById(bookingDto.getItemId());
        }
    }

    @Test
    void saveNewBooking_userNotFound_fail() {
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBooking(bookingDto))
                    .thenReturn(booking);
            when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
            when(userRepository.findById(user2Id)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> bookingService.saveNewBooking(bookingDto, user2Id));

            verify(itemRepository).findById(bookingDto.getItemId());
            verify(userRepository).findById(user2Id);
        }
    }

    @Test
    void updateBooking_shouldBeUpdated() {
        booking.setStatus(BookingStatus.WAITING);
        try (MockedStatic<BookingMapper> mockStaticBooking = mockStatic(BookingMapper.class);
             MockedStatic<ItemMapper> mockStaticItem = mockStatic(ItemMapper.class);
             MockedStatic<UserMapper> mockStaticUser = mockStatic(UserMapper.class)) {
            when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
            when(bookingRepository.save(booking)).thenReturn(booking);
            mockStaticBooking.when(() -> BookingMapper.toSavedBookingDto(booking))
                    .thenReturn(savedBookingDto);
            mockStaticUser.when(() -> UserMapper.toUserBookingDto(user2))
                    .thenReturn(userBookingDto);
            mockStaticItem.when(() -> ItemMapper.toItemBookingDto(item))
                    .thenReturn(itemBookingDto);

            SavedBookingDto savedBookingDtoActual = bookingService
                    .updateBooking(bookingId, userId, true);

            assertEquals(savedBookingDto, savedBookingDtoActual);
            verify(bookingRepository).findById(bookingId);
            verify(bookingRepository).save(booking);
        }
    }

    @Test
    void updateBooking_bookingNotFound_fail() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService
                .updateBooking(bookingId, userId, true));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void updateBooking_bookingNotBelongToUser_fail() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingNotBelongException.class, () -> bookingService
                .updateBooking(bookingId, user2Id, true));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void updateBooking_bookingAlreadyConfirmed_fail() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(BookingAlreadyApprovedException.class, () -> bookingService
                .updateBooking(bookingId, userId, true));

        verify(bookingRepository).findById(bookingId);
    }
}
