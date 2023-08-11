package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @Test
    void getItemById() {
        long itemId = 1;
        long userId = 1;
        ItemDto expectedItemDto = new ItemDto();
        Mockito.when(itemService.getItemById(itemId, userId)).thenReturn(expectedItemDto);

        ItemDto itemDto = itemController.getItemById(itemId, userId);

        assertEquals(expectedItemDto, itemDto);
        verify(itemService, times(1)).getItemById(itemId, userId);
    }

    @Test
    void getAllItemsOfUser() {
        long userId = 1;
        long from = 0;
        long size = 20;
        List<ItemDto> expectedItemDtoList = List.of(new ItemDto());
        Mockito.when(itemService.getAllItemsOfUser(from, size, userId)).thenReturn(expectedItemDtoList);

        List<ItemDto> itemDtoList = itemController.getAllItemsOfUser(userId, from, size);

        assertEquals(expectedItemDtoList, itemDtoList);
        verify(itemService, times(1)).getAllItemsOfUser(from, size, userId);
    }

    @Test
    void saveNewItem() {
        long userId = 1;
        ItemDto expectedItemDto = new ItemDto();
        Mockito.when(itemService.saveNewItem(expectedItemDto, userId)).thenReturn(expectedItemDto);

        ItemDto itemDto = itemController.saveNewItem(expectedItemDto, userId);

        assertEquals(expectedItemDto, itemDto);
        verify(itemService, times(1)).saveNewItem(expectedItemDto, userId);
    }

    @Test
    void updateItem() {
        long userId = 1;
        long itemId = 1;
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        Mockito.when(itemService.updateItem(expectedItemDto, itemId, userId)).thenReturn(expectedItemDto);

        ItemDto itemDto = itemController.updateItem(expectedItemDto, itemId, userId);

        assertEquals(expectedItemDto, itemDto);
        verify(itemService, times(1)).updateItem(expectedItemDto, itemId, userId);
    }


    @Test
    void searchItems() {
        String text = "text";
        long from = 0;
        long size = 20;
        List<ItemDto> expectedItemDtoList = List.of(new ItemDto());
        Mockito.when(itemService.searchItems(from, size, text)).thenReturn(expectedItemDtoList);

        List<ItemDto> itemDtoList = itemController.searchItems(text, from, size);

        assertEquals(expectedItemDtoList, itemDtoList);
        verify(itemService, times(1)).searchItems(from, size, text);
    }

    @Test
    void saveNewComment() {
        long userId = 1;
        long itemId = 1;
        CommentDto expectedCommentDto = new CommentDto();
        Comment comment = new Comment();
        Mockito.when(itemService.saveNewComment(itemId, comment, userId)).thenReturn(expectedCommentDto);

        CommentDto commentDto = itemController.saveNewComment(itemId, comment, userId);

        assertEquals(expectedCommentDto, commentDto);
        verify(itemService, times(1)).saveNewComment(itemId, comment, userId);
    }
}