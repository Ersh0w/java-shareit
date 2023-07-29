package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getAllItemsOfUser(long userId);

    ItemDto saveNewItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long itemId, long userId);

    List<ItemDto> searchItems(String text);

    CommentDto saveNewComment(long itemId, Comment comment, long userId);
}
