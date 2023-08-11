package ru.practicum.shareit.request;

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
class ItemRequestControllerTest {
    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    @Test
    void saveNewRequest() {
        long userId = 1;
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto();
        expectedItemRequestDto.setId(1);
        ItemRequest itemRequest = new ItemRequest();
        Mockito.when(itemRequestService.saveNewRequest(itemRequest, userId))
                .thenReturn(expectedItemRequestDto);

        ItemRequestDto itemRequestDto = itemRequestController.saveNewRequest(itemRequest, userId);

        assertEquals(expectedItemRequestDto, itemRequestDto);
        verify(itemRequestService, times(1))
                .saveNewRequest(itemRequest, userId);
    }

    @Test
    void getRequestsOfUser() {
        long userId = 1;
        List<ItemRequestDto> expectedRequests = List.of(new ItemRequestDto());
        Mockito.when(itemRequestService.getRequestsOfUser(userId))
                .thenReturn(expectedRequests);

        List<ItemRequestDto> itemRequestDtoList = itemRequestController.getRequestsOfUser(userId);

        assertEquals(expectedRequests, itemRequestDtoList);
        verify(itemRequestService, times(1))
                .getRequestsOfUser(userId);
    }

    @Test
    void getRequestById() {
        long userId = 1;
        ItemRequestDto expectedItemRequestDto = new ItemRequestDto();
        expectedItemRequestDto.setId(1);
        Mockito.when(itemRequestService.getRequestById(expectedItemRequestDto.getId(), userId))
                .thenReturn(expectedItemRequestDto);

        ItemRequestDto itemRequestDto = itemRequestController.getRequestById(expectedItemRequestDto.getId(), userId);

        assertEquals(expectedItemRequestDto, itemRequestDto);
        verify(itemRequestService, times(1))
                .getRequestById(expectedItemRequestDto.getId(), userId);
    }

    @Test
    void getAllRequests() {
        long userId = 1;
        long from = 0;
        long size = 20;
        List<ItemRequestDto> expectedRequests = List.of(new ItemRequestDto());
        Mockito.when(itemRequestService.getAllRequests(from, size, userId)).thenReturn(List.of(new ItemRequestDto()));

        List<ItemRequestDto> itemRequestDtoList = itemRequestController.getAllRequests(userId, from, size);

        assertEquals(expectedRequests, itemRequestDtoList);
        verify(itemRequestService, times(1)).getAllRequests(from, size, userId);
    }
}