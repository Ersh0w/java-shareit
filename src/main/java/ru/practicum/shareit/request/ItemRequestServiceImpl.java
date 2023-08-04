package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemRequestDto saveNewRequest(ItemRequest itemRequest, long userId) {
        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(requestor);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(savedItemRequest);
    }

    @Override
    public List<ItemRequestDto> getRequestsOfUser(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId);

        if (!itemRequests.isEmpty()) {
            itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(itemRequests);
            return findAndAttachItemsToRequestDtos(itemRequestDtoList);
        }

        return itemRequestDtoList;
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException("Запрос не найден"));
        List<Item> itemsOfRequest = itemRepository.findAllByRequestId(requestId);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(new ArrayList<>());
        if (!itemsOfRequest.isEmpty()) {
            itemRequestDto.setItems(ItemMapper.toItemForItemRequestDtoList(itemsOfRequest));
        }

        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long from, long size, long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();

        Page<ItemRequest> itemRequestsPage = itemRequestRepository
                .findByRequestorIdNot(PageRequest.of((int) (from / size), (int) size,
                        Sort.by("created").descending()), userId);

        if (itemRequestsPage != null && itemRequestsPage.hasContent()) {
            List<ItemRequest> itemRequestsList = itemRequestsPage.getContent();
            itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(itemRequestsList);
            return findAndAttachItemsToRequestDtos(itemRequestDtoList);
        }

        return itemRequestDtoList;
    }

    protected List<ItemRequestDto> findAndAttachItemsToRequestDtos(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList.forEach(i -> i.setItems(new ArrayList<>()));
        List<Long> itemRequestsIds = itemRequestDtoList.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());

        List<Item> itemsForRequests = itemRepository.findAllByRequestsIds(itemRequestsIds);

        if (!itemsForRequests.isEmpty()) {
            Map<Long, List<Item>> requestIdItemsMap = new HashMap<>();

            for (Item item : itemsForRequests) {
                requestIdItemsMap.computeIfAbsent(item.getRequest().getId(), i -> new ArrayList<>()).add(item);
            }

            for (ItemRequestDto itemRequestDto : itemRequestDtoList) {
                if (requestIdItemsMap.containsKey(itemRequestDto.getId())) {
                    itemRequestDto.setItems(ItemMapper
                            .toItemForItemRequestDtoList(requestIdItemsMap.get(itemRequestDto.getId())));
                }
            }
        }

        return itemRequestDtoList;
    }
}
