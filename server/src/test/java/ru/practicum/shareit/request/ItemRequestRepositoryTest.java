package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;
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
    }

    @Test
    void findAllByRequestorId() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequestorId(user.getId());

        assertEquals(1, result.size());
        assertEquals(user, result.get(0).getRequestor());
        assertEquals(itemRequest.getId(), result.get(0).getId());
        assertEquals(itemRequest.getDescription(), result.get(0).getDescription());
    }

    @Test
    void findAllByItemsIds() {
        List<ItemRequest> result = itemRequestRepository.findByRequestorIdNot(Pageable.unpaged(),
                user.getId()).toList();

        assertEquals(1, result.size());
        assertEquals(user2, result.get(0).getRequestor());
        assertEquals(itemRequest2.getId(), result.get(0).getId());
        assertEquals(itemRequest2.getDescription(), result.get(0).getDescription());
    }

    @AfterAll
    void afterAll() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE requests ALTER COLUMN ID RESTART WITH 1;");
    }
}
