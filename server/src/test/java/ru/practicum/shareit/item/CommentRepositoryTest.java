package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    Item item;
    Item item2;
    User user;
    User user2;
    Comment comment;
    Comment comment2;
    long itemId = 1;

    @BeforeAll
    void beforeAll() {
        user = User.builder().name("user").email("user@mail.com").build();
        user2 = User.builder().name("user2").email("user2@mail.com").build();
        userRepository.save(user);
        userRepository.save(user2);
        item = Item.builder().name("item").description("item Description").available(true).owner(user)
                .build();
        item2 = Item.builder().name("item2").description("item2 Description").available(true).owner(user2)
                .build();
        itemRepository.save(item);
        itemRepository.save(item2);
        System.out.println(item);
        System.out.println(item2);
        comment = Comment.builder().author(user2).item(item).text("comment1 text").created(LocalDateTime.now()).build();
        comment2 = Comment.builder().author(user).item(item2).text("comment2 text").created(LocalDateTime.now()).build();
        System.out.println(comment);
        System.out.println(comment2);
        commentRepository.save(comment);
        commentRepository.save(comment2);
        System.out.println(comment);
        System.out.println(comment2);
    }

    @Test
    void findAllByItemsIds() {
        List<Comment> result = commentRepository.findAllByItemsIds(List.of(itemId));

        assertEquals(1, result.size());
        assertEquals(comment.getId(), result.get(0).getId());
        assertEquals(comment.getText(), result.get(0).getText());
        assertEquals(user2.getId(), result.get(0).getAuthor().getId());
    }

    @AfterAll
    void afterAll() {
        commentRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
        jdbcTemplate.execute("ALTER TABLE comments ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;");
        jdbcTemplate.execute("ALTER TABLE items ALTER COLUMN ID RESTART WITH 1;");
    }
}
