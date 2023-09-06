package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
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
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
    Item item;
    Item item2;
    User user;
    User user2;
    ItemRequest itemRequest;
    ItemRequest itemRequest2;

    @BeforeAll
    void beforeAll() {
        user = User.builder().name("user").email("user@mail.com").build();
        user2 = User.builder().name("user2").email("user2@mail.com").build();
        userRepository.save(user);
        userRepository.save(user2);
        itemRequest = ItemRequest.builder().description("ItemRequest description").created(LocalDateTime.now())
                .requestor(user).build();
        itemRequest2 = ItemRequest.builder().description("ItemRequest2 description").created(LocalDateTime.now())
                .requestor(user2).build();
        itemRequestRepository.save(itemRequest);
        itemRequestRepository.save(itemRequest2);
        item = Item.builder().name("item").description("item Description").available(true).owner(user)
                .request(itemRequest).build();
        item2 = Item.builder().name("item2").description("item2 Description").available(true).owner(user2)
                .request(itemRequest2).build();
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    @Test
    void findById() {
        Optional<Item> result = itemRepository.findById(item.getId());

        assertTrue(result.isPresent());
        assertEquals(item.getId(), result.get().getId());
        assertEquals(item.getName(), result.get().getName());
        assertEquals(item.getDescription(), result.get().getDescription());
        assertEquals(user.getId(), result.get().getOwner().getId());
        assertEquals(itemRequest.getId(), result.get().getRequest().getId());
    }

    @Test
    void findAllByOwnerId() {
        List<Item> result = itemRepository.findAllByOwnerId(Pageable.unpaged(), user.getId());

        assertItemDetails_findAllByRequestId_findAllByRequestsIds_findAllByOwnerId(result);
    }

    @Test
    void findByIdAndOwnerId() {
        Optional<Item> result = itemRepository.findByIdAndOwnerId(item.getId(), user.getId());

        assertTrue(result.isPresent());
        assertEquals(item.getId(), result.get().getId());
        assertEquals(item.getName(), result.get().getName());
        assertEquals(item.getDescription(), result.get().getDescription());
        assertEquals(user.getId(), result.get().getOwner().getId());
        assertEquals(itemRequest.getId(), result.get().getRequest().getId());
    }

    @Test
    void searchItems() {
        List<Item> result = itemRepository.searchItems(Pageable.unpaged(), "item2").toList();

        assertEquals(1, result.size());
        assertEquals(item2.getId(), result.get(0).getId());
        assertEquals(item2.getName(), result.get(0).getName());
        assertEquals(item2.getDescription(), result.get(0).getDescription());
        assertEquals(user2.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest2.getId(), result.get(0).getRequest().getId());
    }

    @Test
    void findAllByRequestId() {
        List<Item> result = itemRepository.findAllByRequestId(itemRequest.getId());

        assertItemDetails_findAllByRequestId_findAllByRequestsIds_findAllByOwnerId(result);
    }

    @Test
    void findAllByRequestsIds() {
        List<Item> result = itemRepository.findAllByRequestsIds(List.of(itemRequest.getId()));

        assertItemDetails_findAllByRequestId_findAllByRequestsIds_findAllByOwnerId(result);
    }

    @AfterAll
    void afterAll() {
        itemRepository.deleteAll();
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE requests ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE items ALTER COLUMN ID RESTART WITH 1;");
    }

    private void assertItemDetails_findAllByRequestId_findAllByRequestsIds_findAllByOwnerId(List<Item> result) {
        assertEquals(1, result.size());
        assertEquals(item.getId(), result.get(0).getId());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        assertEquals(user.getId(), result.get(0).getOwner().getId());
        assertEquals(itemRequest.getId(), result.get(0).getRequest().getId());
    }
}

