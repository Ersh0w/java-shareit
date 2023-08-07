package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto saveNewRequest(ItemRequest itemRequest, long userId);

    List<ItemRequestDto> getRequestsOfUser(long userId);

    ItemRequestDto getRequestById(long requestId, long userId);

    List<ItemRequestDto> getAllRequests(long from, long size, long userId);
}
