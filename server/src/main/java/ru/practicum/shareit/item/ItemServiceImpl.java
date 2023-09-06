package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        List<ItemDto> itemDto = List.of(ItemMapper.toItemDto(item));

        if (item.getOwner().getId() == userId) {
            itemDto = finaAndAttachNearestBookingsToItemsDto(itemDto, List.of(item.getId()));
        }

        return findAndAttachCommentsToItemsDto(itemDto, List.of(item.getId())).get(0);
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(long from, long size, long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(PageRequest.of((int) (from / size), (int) size), userId);
        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<ItemDto> itemsDto = finaAndAttachNearestBookingsToItemsDto(ItemMapper.toItemDtoList(items), itemsIds);

        return findAndAttachCommentsToItemsDto(itemsDto, itemsIds);
    }

    @Override
    @Transactional
    public ItemDto saveNewItem(ItemDto itemDto, long userId) {
        Item itemToSave = ItemMapper.toItem(itemDto);
        itemToSave.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
        if (null != itemDto.getRequestId()) {
            itemToSave.setRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException("Запрос на вещь не найден")));
        }
        Item savedItem = itemRepository.save(itemToSave);
        log.info("Добавлена вещь: {}", savedItem.toString());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        Item itemToUpdate = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new ItemNotBelongToUserException("Вещь для обновления не найдена у пользователя"));

        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(itemToUpdate);
        log.info("Обновлена вещь: {}", updatedItem.toString());

        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItems(long from, long size, String text) {
        List<ItemDto> itemsDtoToReturn = new ArrayList<>();
        if (!text.isEmpty()) {
            Page<Item> itemsPage = itemRepository.searchItems(PageRequest.of((int) (from / size), (int) size), text);
            if (itemsPage != null && itemsPage.hasContent()) {
                List<Item> foundItems = itemsPage.getContent();
                itemsDtoToReturn = ItemMapper.toItemDtoList(foundItems);
            }
        }

        return itemsDtoToReturn;
    }

    @Override
    @Transactional
    public CommentDto saveNewComment(long itemId, Comment comment, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Вещь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        List<Booking> booking = bookingRepository.findBookingByItemIdAndBookerId(itemId, userId, PageRequest.of(0, 1));
        if (booking.isEmpty()) {
            throw new CommentWithoutBookingException("Нельзя оставлять отзыв без завершенного бронирования");
        }
        if (booking.get(0).getEnd().isAfter(LocalDateTime.now())) {
            throw new CommentBeforeBookingEndException("Нельзя оставлять отзыв до истечения бронирования");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    protected List<ItemDto> findAndAttachCommentsToItemsDto(List<ItemDto> items, List<Long> itemsIds) {
        List<Comment> comments = commentRepository.findAllByItemsIds(itemsIds);
        items.forEach(i -> i.setComments(new ArrayList<>()));

        if (!comments.isEmpty()) {
            Map<Long, List<Comment>> commentsByItemId = comments.stream()
                    .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
            items.forEach(item -> {
                List<Comment> commentsForItem = commentsByItemId.getOrDefault(item.getId(), new ArrayList<>());
                item.setComments(CommentMapper.toCommentDtoList(commentsForItem));
            });
            return items;
        } else {
            return items;
        }
    }

    protected List<ItemDto> finaAndAttachNearestBookingsToItemsDto(List<ItemDto> itemsDto, List<Long> itemsIds) {
        List<Booking> bookings = bookingRepository.findApprovedItemsBookings(itemsIds);

        Map<Long, Optional<Booking>> nearestPastBookings = bookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.groupingBy(b -> b.getItem().getId(),
                        Collectors.maxBy(Comparator.comparing(Booking::getStart))));

        Map<Long, Optional<Booking>> nearestFutureBookings = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.groupingBy(b -> b.getItem().getId(),
                        Collectors.minBy(Comparator.comparing(Booking::getStart))));

        for (ItemDto item : itemsDto) {
            if (nearestPastBookings.get(item.getId()) != null) {
                item.setLastBooking(BookingMapper.toBookingItemDto(nearestPastBookings.get(item.getId()).get()));
            }
            if (nearestFutureBookings.get(item.getId()) != null) {
                item.setNextBooking(BookingMapper.toBookingItemDto(nearestFutureBookings.get(item.getId()).get()));
            }
        }

        return itemsDto;
    }
}
