package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        log.info("получен запрос на получение вещи с id " + itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping()
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("получен запрос на получение всех вещей пользователя с id " + userId);
        return itemService.getAllItemsOfUser(userId);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestBody @Valid ItemDto itemDto,
                               @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("получен запрос на добавление вещи пользователю с id " + userId);
        return itemService.saveNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("получен запрос на обновление вещи с id " + itemId + ", пользователя с id " + userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("получен запрос на поиск вещей по запросу: " + text);
        return itemService.searchItems(text);
    }
}
