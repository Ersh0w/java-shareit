package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
        ItemDto getItemById(Integer itemId);

        List<ItemDto> getAllItemsOfUser(Integer userId);

        ItemDto saveNewItem(ItemDto itemDto, Integer userId);

        ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId);

        List<ItemDto> searchItems(String text);
}
