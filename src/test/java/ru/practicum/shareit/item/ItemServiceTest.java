package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    @InjectMocks
    private ItemServiceImpl itemService;

    Item item;
    ItemDto itemDto;
    Comment comment;
    CommentDto commentDto;
    ItemDto itemDtoWithBookings;
    ItemDto itemDtoWithBookingsAndComments;
    User user;
    BookingItemDto bookingItemDto;
    ItemRequest itemRequest;
    PageRequest pageRequest = PageRequest.of(2, 5);
    Booking booking;
    long userId = 1;
    long itemId = 1;

    @BeforeEach
    void setUp() {
        user = User.builder().id(userId).name("name").email("mail@mail.com").build();
        itemRequest = ItemRequest.builder().id(1L).created(LocalDateTime.now().minusDays(1))
                .description("ItemRequest Description").requestor(user).build();
        item = Item.builder().id(itemId).available(true).description("item description").request(itemRequest)
                .owner(user).build();
        comment = Comment.builder().id(1).author(user).item(item).text("Comment text")
                .created(LocalDateTime.now().minusDays(1)).build();
        commentDto = CommentDto.builder().id(comment.getId()).authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).text(comment.getText()).build();
        itemDto = ItemDto.builder().id(item.getId()).description(item.getDescription()).name(item.getName())
                .requestId(itemRequest.getId()).available(item.getAvailable()).build();
        bookingItemDto = BookingItemDto.builder().id(1).bookerId(user.getId()).build();
        itemDtoWithBookings = ItemDto.builder().id(item.getId()).description(item.getDescription()).name(item.getName())
                .available(item.getAvailable()).nextBooking(bookingItemDto).build();
        itemDtoWithBookingsAndComments = itemDtoWithBookings;
        itemDtoWithBookingsAndComments.setComments(List.of(commentDto));
        booking = Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(2))
                .item(item).booker(user).status(BookingStatus.APPROVED).id(1).build();
    }

    @Test
    void getItemById_shouldBeFound() {
        try (MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            mockStaticItem.when(() -> ItemMapper.toItemDto(item))
                    .thenReturn(itemDto);
            doReturn(List.of(itemDtoWithBookings)).when(itemService)
                    .finaAndAttachNearestBookingsToItemsDto(List.of(itemDto), List.of(item.getId()));
            doReturn(List.of(itemDtoWithBookingsAndComments)).when(itemService)
                    .findAndAttachCommentsToItemsDto(List.of(itemDtoWithBookings), List.of(item.getId()));

            ItemDto itemDtoActual = itemService.getItemById(itemId, userId);

            assertEquals(itemDtoWithBookings, itemDtoActual);
            verify(itemRepository).findById(itemId);
            verify(itemService).finaAndAttachNearestBookingsToItemsDto(List.of(itemDto), List.of(item.getId()));
            verify(itemService).findAndAttachCommentsToItemsDto(List.of(itemDtoWithBookings), List.of(item.getId()));
        }
    }

    @Test
    void getItemById_shouldNotBeFound_fail() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItemById(itemId, userId));

        verify(itemRepository).findById(itemId);
    }

    @Test
    void getAllItemsOfUser_shouldBeFound() {
        try (MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            when(itemRepository.findAllByOwnerId(pageRequest, userId)).thenReturn(List.of(item));
            mockStaticItem.when(() -> ItemMapper.toItemDtoList(List.of(item)))
                    .thenReturn(List.of(itemDto));
            doReturn(List.of(itemDtoWithBookings)).when(itemService)
                    .finaAndAttachNearestBookingsToItemsDto(List.of(itemDto), List.of(item.getId()));
            doReturn(List.of(itemDtoWithBookingsAndComments)).when(itemService)
                    .findAndAttachCommentsToItemsDto(List.of(itemDtoWithBookings), List.of(item.getId()));

            List<ItemDto> itemDtoListActual = itemService.getAllItemsOfUser(10, 5, userId);

            assertEquals(List.of(itemDtoWithBookings), itemDtoListActual);
            verify(itemService).finaAndAttachNearestBookingsToItemsDto(List.of(itemDto), List.of(item.getId()));
            verify(itemService).findAndAttachCommentsToItemsDto(List.of(itemDtoWithBookings), List.of(item.getId()));
        }
    }

    @Test
    void saveNewItem_shouldBeSaved() {
        try (MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            mockStaticItem.when(() -> ItemMapper.toItem(itemDto))
                    .thenReturn(item);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(itemRequestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(itemRequest));
            when(itemRepository.save(item)).thenReturn(item);
            mockStaticItem.when(() -> ItemMapper.toItemDto(item))
                    .thenReturn(itemDto);

            ItemDto itemDtoActual = itemService.saveNewItem(itemDto, userId);

            assertEquals(itemDto, itemDtoActual);
            verify(userRepository).findById(userId);
            verify(itemRequestRepository).findById(itemDto.getRequestId());
            verify(itemRepository).save(item);
        }
    }

    @Test
    void updateItem_shouldBeUpdated() {
        try (MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            mockStaticItem.when(() -> ItemMapper.toItemDto(item))
                    .thenReturn(itemDto);
            when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.of(item));
            when(itemRepository.save(item)).thenReturn(item);

            ItemDto itemDtoActual = itemService.updateItem(itemDto, itemId, userId);

            assertEquals(itemDto, itemDtoActual);
            verify(itemRepository).findByIdAndOwnerId(itemId, userId);
            verify(itemRepository).save(item);
        }
    }

    @Test
    void updateItem_itemNotBelongToUser_fail() {
        when(itemRepository.findByIdAndOwnerId(itemId, userId)).thenReturn(Optional.empty());

        assertThrows(ItemNotBelongToUserException.class, () -> itemService.updateItem(itemDto, itemId, userId));

        verify(itemRepository).findByIdAndOwnerId(itemId, userId);
    }

    @Test
    void searchItems() {
        try (MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            mockStaticItem.when(() -> ItemMapper.toItemDtoList(List.of(item)))
                    .thenReturn(List.of(itemDto));
            when(itemRepository.searchItems(pageRequest, "text")).thenReturn(new PageImpl<>(List.of(item)));

            List<ItemDto> itemDtoActual = itemService.searchItems(10, 5, "text");

            assertEquals(List.of(itemDto), itemDtoActual);
            verify(itemService).searchItems(10, 5, "text");
        }
    }

    @Test
    void saveNewComment_shouldBeSaved() {
        try (MockedStatic mockStaticComment = mockStatic(CommentMapper.class)) {
            mockStaticComment.when(() -> CommentMapper.toCommentDto(comment))
                    .thenReturn(commentDto);
            when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(bookingRepository.findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1)))
                    .thenReturn(List.of(booking));
            when(commentRepository.save(comment)).thenReturn(comment);

            CommentDto commentDtoActual = itemService.saveNewComment(itemId, comment, userId);

            assertEquals(commentDto, commentDtoActual);
            verify(itemRepository).findById(itemId);
            verify(userRepository).findById(userId);
            verify(bookingRepository).findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1));
            verify(commentRepository).save(comment);
        }
    }

    @Test
    void saveNewComment_bookingNotFound_fail() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1)))
                .thenReturn(new ArrayList<Booking>());

        assertThrows(CommentWithoutBookingException.class, () -> itemService.saveNewComment(itemId, comment, userId));

        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1));
    }

    @Test
    void saveNewComment_bookingIsActive_fail() {
        booking.setEnd(LocalDateTime.now().plusDays(1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1)))
                .thenReturn(List.of(booking));

        assertThrows(CommentBeforeBookingEndException.class, () -> itemService.saveNewComment(itemId, comment, userId));

        verify(itemRepository).findById(itemId);
        verify(userRepository).findById(userId);
        verify(bookingRepository).findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1));
    }

    @Test
    void findAndAttachCommentsToItemsDto_shouldBeAttached() {
        try (MockedStatic mockStaticComment = mockStatic(CommentMapper.class)) {
            mockStaticComment.when(() -> CommentMapper.toCommentDtoList(List.of(comment)))
                    .thenReturn(List.of(commentDto));
            when(commentRepository.findAllByItemsIds(List.of(itemId))).thenReturn(List.of(comment));

            List<ItemDto> itemDtoListActual = itemService.findAndAttachCommentsToItemsDto(List.of(itemDto), List.of(itemId));

            assertEquals(List.of(itemDto), itemDtoListActual);
            verify(commentRepository).findAllByItemsIds(List.of(itemId));
        }
    }

    @Test
    void finaAndAttachNearestBookingsToItemsDto_shouldBeAttached() {
        try (MockedStatic mockStaticBooking = mockStatic(BookingMapper.class)) {
            mockStaticBooking.when(() -> BookingMapper.toBookingItemDto(booking))
                    .thenReturn(bookingItemDto);
            when(bookingRepository.findApprovedItemsBookings(List.of(itemId))).thenReturn(List.of(booking));

            List<ItemDto> itemDtoListActual = itemService.finaAndAttachNearestBookingsToItemsDto(List.of(itemDto), List.of(itemId));

            assertEquals(List.of(itemDto), itemDtoListActual);
            verify(bookingRepository).findApprovedItemsBookings(List.of(itemId));
        }
    }
}
