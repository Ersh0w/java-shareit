package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/items")
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable long itemId,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на получение вещи с id " + itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsOfUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                                    @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на получение всех вещей пользователя с id " + userId);
        return itemClient.getAllItemsOfUser(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> saveNewItem(@RequestBody @Valid ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление вещи пользователю с id " + userId);
        return itemClient.saveNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable long itemId,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на обновление вещи с id " + itemId + ", пользователя с id " + userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                              @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на поиск вещей по запросу: " + text);
        return itemClient.searchItems(from, size, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveNewComment(@PathVariable long itemId,
                                                 @RequestBody @Valid Comment comment,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление комментария к вещи с id " + itemId + " от пользователя с id " + userId);
        return itemClient.saveNewComment(itemId, comment, userId);
    }
}
