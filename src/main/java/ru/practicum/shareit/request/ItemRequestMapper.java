package ru.practicum.shareit.request;

import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

        for (ItemRequest i : itemRequests) {
            itemRequestDtoList.add(toItemRequestDto(i));
        }

        return itemRequestDtoList;
    }
}
