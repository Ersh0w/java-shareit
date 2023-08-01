package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@Validated
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveNewRequest(@RequestBody @Valid ItemRequest itemRequest,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на добавление запроса от пользователя id " + userId);
        return itemRequestService.saveNewRequest(itemRequest, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на получение запросов от пользователя с id " + userId);
        return itemRequestService.getRequestsOfUser(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("получен запрос на получение запроса с id " + requestId + " от пользователя с id" + userId);
        return itemRequestService.getRequestById(requestId, userId);
    }

    @GetMapping("all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero long from,
                                               @RequestParam(defaultValue = "20") @Positive long size) {
        log.info("получен запрос на получение всех запросов от пользователя с id " + userId);
        return itemRequestService.getAllRequests(from, size, userId);
    }
}