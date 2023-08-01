package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    Item item;
    User user;
    ItemRequest itemRequest;

    @BeforeAll
    void beforeAll() {
        user = User.builder().name("Test user").email("test@mail.com").build();
        userRepository.save(user);
        itemRequest = ItemRequest.builder().description("ItemRequest description").created(LocalDateTime.now())
                .requestor(user).build();
        itemRequestRepository.save(itemRequest);
        item = Item.builder().name("Test Item").description("Test Description").available(true).owner(user)
                .request(itemRequest).build();
        itemRepository.save(item);
    }

    @Test
    void testFindById() {
        Optional<Item> result = itemRepository.findById(item.getId());

        assertTrue(result.isPresent());
        assertEquals(item.getId(), result.get().getId());
        assertEquals(item.getName(), result.get().getName());
        assertEquals(item.getDescription(), result.get().getDescription());
        assertEquals(user.getId(), result.get().getOwner().getId());
        assertEquals(itemRequest.getId(), result.get().getRequest().getId());
    }

    @Test
    void testFindAllByOwnerId() {
        List<Item> result = itemRepository.findAllByOwnerId(Pageable.unpaged(), user.getId());

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(user.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), result.get(0).getRequest().getId());
    }

    @Test
    void testFindByIdAndOwnerId() {
        Optional<Item> result = itemRepository.findByIdAndOwnerId(item.getId(), user.getId());

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get().getId());
        assertEquals(item.getName(), result.get().getName());
        assertEquals(item.getDescription(), result.get().getDescription());
        assertEquals(user.getId(), result.get().getOwner().getId());
        assertEquals(itemRequest.getId(), result.get().getRequest().getId());
    }

    @Test
    void testSearchItems() {
        List<Item> result = itemRepository.searchItems(Pageable.unpaged(), "Item").toList();

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(user.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), result.get(0).getRequest().getId());
    }

    @Test
    void testFindAllByRequestId() {
        List<Item> result = itemRepository.findAllByRequestId(itemRequest.getId());

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(user.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), result.get(0).getRequest().getId());
    }

    @Test
    void testFindAllByRequestsIds() {
        List<Item> result = itemRepository.findAllByRequestsIds(List.of(itemRequest.getId()));

        assertFalse(result.isEmpty());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(user.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), result.get(0).getRequest().getId());
    }
}

