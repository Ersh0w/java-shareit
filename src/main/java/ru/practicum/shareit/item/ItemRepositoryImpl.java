package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Integer, List<Item>> items = new HashMap<>();
    private Integer idCounter = 0;

    @Override
    public Item getItemById(Integer id) {
        for (List<Item> list : items.values()) {
            for (Item item : list) {
                if (Objects.equals(item.getId(), id)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public List<Item> getAllItemsOfUser(Integer userId) {
        return items.get(userId);
    }

    @Override
    public Item saveNewItem(Item item) {
        item.setId(getId());
        if (items.containsKey(item.getOwner().getId())) {
            List<Item> listToUpdate = items.get(item.getOwner().getId());
            listToUpdate.add(item);
            items.put(item.getOwner().getId(), listToUpdate);
        } else {
            List<Item> list = new ArrayList<>();
            list.add(item);
            items.put(item.getOwner().getId(), list);
        }
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        List<Item> itemsOfUser = items.get(item.getOwner().getId());
        Item itemToReturn = null;
        for (Item itemOfUser : itemsOfUser) {
            if (Objects.equals(itemOfUser.getId(), item.getId())) {
                if (item.getName() != null) {
                    itemOfUser.setName(item.getName());
                }
                if (item.getDescription() != null) {
                    itemOfUser.setDescription(item.getDescription());
                }
                if (item.getAvailable() != null) {
                    itemOfUser.setAvailable(item.getAvailable());
                }
                itemToReturn = itemOfUser;
                break;
            }
        }
        items.put(item.getOwner().getId(), itemsOfUser);
        return itemToReturn;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> foundItems = new ArrayList<>();
        String textToFindLowerCase = text.toLowerCase();
        items.forEach((key, value) -> value.forEach(item -> {
            String itemNameLowerCase = item.getName().toLowerCase();
            String itemDescriptionLowerCase = item.getDescription().toLowerCase();
            if (itemNameLowerCase.contains(textToFindLowerCase)
                    || itemDescriptionLowerCase.contains(textToFindLowerCase)
                    && item.getAvailable()) {
                foundItems.add(item);
            }
        }));

        return foundItems;
    }

    @Override
    public boolean isItemPresentById(Integer id) {
            for (List<Item> list : items.values()) {
                for (Item item : list) {
                    if (Objects.equals(item.getId(), id)) {
                        return true;
                    }
                }
            }
        return false;
    }

    @Override
    public boolean isItemBelongsToUser(Integer itemId, Integer userId) {
        if (items.get(userId) != null) {
            for (Item item : items.get(userId)) {
                if (Objects.equals(item.getId(), itemId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Integer getId() {
        idCounter++;
        return idCounter;
    }
}
