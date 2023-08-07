package ru.practicum.shareit.item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();

        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }

        return itemDto;
    }

    public static List<ItemDto> toItemDtoList(List<Item> items) {
        List<ItemDto> itemsDto = new ArrayList<>();

        for (Item item : items) {
            itemsDto.add(ItemMapper.toItemDto(item));
        }

        return itemsDto;
    }


    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemBookingDto toItemBookingDto(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static ItemForItemRequestDto toItemForItemRequestDto(Item item) {
        return ItemForItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    public static List<ItemForItemRequestDto> toItemForItemRequestDtoList(List<Item> items) {
        List<ItemForItemRequestDto> itemForItemRequestDtoList = new ArrayList<>();

        for (Item item : items) {
            itemForItemRequestDtoList.add(toItemForItemRequestDto(item));
        }

        return itemForItemRequestDtoList;
    }
}
