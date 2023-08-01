package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на получение вещи с id " + itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                           @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на получение всех вещей пользователя с id " + userId);
        return itemService.getAllItemsOfUser(from, size, userId);
    }

    @PostMapping
    public ItemDto saveNewItem(@RequestBody @Valid ItemDto itemDto,
                               @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление вещи пользователю с id " + userId);
        return itemService.saveNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable long itemId,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на обновление вещи с id " + itemId + ", пользователя с id " + userId);
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                     @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на поиск вещей по запросу: " + text);
        return itemService.searchItems(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveNewComment(@PathVariable long itemId,
                                     @RequestBody @Valid Comment comment,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление комментария к вещи с id " + itemId + " от пользователя с id " + userId);
        return itemService.saveNewComment(itemId, comment, userId);
    }
}
