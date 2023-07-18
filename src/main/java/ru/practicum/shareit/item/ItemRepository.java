package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item getItemById(Integer id);

    List<Item> getAllItemsOfUser(Integer userId);

    Item saveNewItem(Item item);

    Item updateItem(Item item);

    List<Item> searchItems(String text);

    boolean isItemBelongsToUser(Integer itemId, Integer userId);

    boolean isItemPresentById(Integer id);
}
