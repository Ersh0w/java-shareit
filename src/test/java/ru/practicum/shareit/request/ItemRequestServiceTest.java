package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Spy
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    long userId = 1;
    long itemRequestId = 1;
    long itemId;
    User requestor;

    Item item;
    ItemRequest itemRequest;
    ItemForItemRequestDto itemForItemRequestDtoExpected;
    ItemRequestDto itemRequestDto;
    ItemRequestDto itemRequestDtoExpected;
    List<ItemRequestDto> emptyItemRequestDtoList = new ArrayList<>();
    PageRequest pageRequest = PageRequest.of(2, 5, Sort.by("created").descending());

    @BeforeEach
    void setUp() {
        requestor = User.builder().id(userId).name("name").email("mail@mail.com").build();
        itemRequest = ItemRequest.builder().id(itemRequestId).created(LocalDateTime.now())
                .requestor(requestor).description("request description").build();
        item = Item.builder().id(itemId).available(true).request(itemRequest).description("item description")
                .owner(requestor).build();
        itemForItemRequestDtoExpected = ItemForItemRequestDto.builder().id(item.getId()).requestId(itemRequest.getId())
                .description(item.getDescription()).available(item.getAvailable()).build();
        itemRequestDtoExpected = ItemRequestDto.builder().id(itemRequestId)
                .description(itemRequest.getDescription()).created(itemRequest.getCreated())
                .items(List.of(itemForItemRequestDtoExpected)).build();
        itemRequestDto = ItemRequestDto.builder().id(itemRequestId).description(itemRequest.getDescription())
                .created(itemRequest.getCreated()).build();
    }

    @Test
    void saveNewRequest_shouldBeSaved() {
        try (MockedStatic mockStatic = mockStatic(ItemRequestMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
            when(itemRequestRepository.save(itemRequest)).thenReturn(itemRequest);
            mockStatic.when(() -> ItemRequestMapper.toItemRequestDto(itemRequest)).thenReturn(itemRequestDtoExpected);

            ItemRequestDto itemRequestDtoActual = itemRequestService.saveNewRequest(itemRequest, userId);

            assertEquals(itemRequestDtoExpected, itemRequestDtoActual);
            verify(itemRequestRepository).save(itemRequest);
        }
    }

    @Test
    void saveNewRequest_userNotFound_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.saveNewRequest(itemRequest, userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void getRequestsOfUser_shouldBeFound() {
        try (MockedStatic mockStatic = mockStatic(ItemRequestMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
            when(itemRequestRepository.findAllByRequestorId(userId)).thenReturn(List.of(itemRequest));
            mockStatic.when(() -> ItemRequestMapper.toItemRequestDtoList(List.of(itemRequest)))
                    .thenReturn(List.of(itemRequestDtoExpected));
            doReturn(List.of(itemRequestDtoExpected)).when(itemRequestService)
                    .findAndAttachItemsToRequestDtos(List.of(itemRequestDtoExpected));

            List<ItemRequestDto> itemRequestDtoActualList = itemRequestService.getRequestsOfUser(userId);

            assertEquals(List.of(itemRequestDtoExpected), itemRequestDtoActualList);
            verify(itemRequestRepository).findAllByRequestorId(userId);
            verify(userRepository).findById(userId);
            verify(itemRequestService).findAndAttachItemsToRequestDtos(List.of(itemRequestDtoExpected));
        }
    }

    @Test
    void getRequestsOfUser_NothingFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByRequestorId(userId)).thenReturn(new ArrayList<>());

        List<ItemRequestDto> itemRequestDtoActualList = itemRequestService.getRequestsOfUser(userId);

        assertEquals(emptyItemRequestDtoList, itemRequestDtoActualList);
        verify(itemRequestRepository).findAllByRequestorId(userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getRequestById_shouldBeFound() {
        try (MockedStatic mockStaticItemRequest = mockStatic(ItemRequestMapper.class);
             MockedStatic mockStaticItem = mockStatic(ItemMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
            when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
            when(itemRepository.findAllByRequestId(itemRequest.getId())).thenReturn(List.of(item));
            mockStaticItemRequest.when(() -> ItemRequestMapper.toItemRequestDto(itemRequest))
                    .thenReturn(itemRequestDtoExpected);
            mockStaticItem.when(() -> ItemMapper.toItemForItemRequestDtoList(List.of(item)))
                    .thenReturn(List.of(itemForItemRequestDtoExpected));

            ItemRequestDto itemRequestDtoActual = itemRequestService.getRequestById(itemRequestId, userId);

            assertEquals(itemRequestDtoExpected, itemRequestDtoActual);
            verify(itemRequestRepository).findById(itemRequestId);
            verify(userRepository).findById(userId);
        }
    }

    @Test
    void getRequestById_requestNotFound_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(ItemRequestNotFoundException.class, () -> itemRequestService.getRequestById(itemRequestId, userId));

        verify(itemRequestRepository).findById(itemRequestId);
        verify(userRepository).findById(userId);
    }

    @Test
    void getAllRequests_shouldBeFound() {
        try (MockedStatic mockStatic = mockStatic(ItemRequestMapper.class)) {
            when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
            when(itemRequestRepository.findByRequestorIdNot(pageRequest, userId))
                    .thenReturn(new PageImpl<>(List.of(itemRequest)));
            mockStatic.when(() -> ItemRequestMapper.toItemRequestDtoList(List.of(itemRequest)))
                    .thenReturn(List.of(itemRequestDtoExpected));
            doReturn(List.of(itemRequestDtoExpected)).when(itemRequestService)
                    .findAndAttachItemsToRequestDtos(List.of(itemRequestDtoExpected));

            List<ItemRequestDto> itemRequestDtoActualList = itemRequestService.getAllRequests(10, 5, userId);

            assertEquals(List.of(itemRequestDtoExpected), itemRequestDtoActualList);
            verify(itemRequestRepository).findByRequestorIdNot(pageRequest, userId);
            verify(userRepository).findById(userId);
            verify(itemRequestService).findAndAttachItemsToRequestDtos(List.of(itemRequestDtoExpected));
        }
    }

    @Test
    void getAllRequests_shouldNotBeFound_fail() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findByRequestorIdNot(pageRequest, userId))
                .thenReturn(new PageImpl<>(List.of()));

        List<ItemRequestDto> itemRequestDtoActualList = itemRequestService.getAllRequests(10, 5, userId);

        assertEquals(new ArrayList<ItemRequestDto>(), itemRequestDtoActualList);
        verify(itemRequestRepository).findByRequestorIdNot(pageRequest, userId);
        verify(userRepository).findById(userId);
    }

    @Test
    void findAndAttachItemsToRequestDtos() {
        try (MockedStatic mockStatic = mockStatic(ItemMapper.class)) {
            when(itemRepository.findAllByRequestsIds(List.of(itemRequestId))).thenReturn(List.of(item));
            mockStatic.when(() -> ItemMapper.toItemForItemRequestDtoList(List.of(item)))
                    .thenReturn(List.of(itemForItemRequestDtoExpected));

            List<ItemRequestDto> itemRequestDtoActualList = itemRequestService
                    .findAndAttachItemsToRequestDtos(List.of(itemRequestDto));

            assertEquals(List.of(itemRequestDtoExpected), itemRequestDtoActualList);
            verify(itemRepository).findAllByRequestsIds(List.of(itemRequestId));
        }
    }
}


