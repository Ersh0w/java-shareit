package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotBelongToUserException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto getItemById(Integer itemId) {
        if (!itemRepository.isItemPresentById(itemId)) {
            throw new ItemNotFoundException("Вещь не найдена");
        }

        return ItemMapper.toItemDto(itemRepository.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllItemsOfUser(Integer userId) {
        if (!userRepository.isUserPresentById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item item : itemRepository.getAllItemsOfUser(userId)) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }

        return itemsDto;
    }

    @Override
    public ItemDto saveNewItem(ItemDto itemDto, Integer userId) {
        if (!userRepository.isUserPresentById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        Item itemToSave = ItemMapper.toItem(itemDto);
        itemToSave.setOwner(userRepository.getUserById(userId));
        Item savedItem = itemRepository.saveNewItem(itemToSave);
        log.info("Добавлена вещь: {}", savedItem.toString());
        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        if (!userRepository.isUserPresentById(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (!itemRepository.isItemBelongsToUser(itemId, userId)) {
            throw new ItemNotBelongToUserException("Вещь для обновления не найдена у пользователя");
        }
        Item itemToUpdate = ItemMapper.toItem(itemDto);
        itemToUpdate.setOwner(userRepository.getUserById(userId));
        itemToUpdate.setId(itemId);
        Item updatedItem = itemRepository.updateItem(itemToUpdate);
        log.info("Обновлена вещь: {}", updatedItem.toString());
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> itemsDtoToReturn = new ArrayList<>();
        if (!text.isEmpty()) {
            List<Item> itemsToReturn = itemRepository.searchItems(text);
            for (Item item : itemsToReturn) {
                itemsDtoToReturn.add(ItemMapper.toItemDto(item));
            }
        }
        return itemsDtoToReturn;
    }
}
