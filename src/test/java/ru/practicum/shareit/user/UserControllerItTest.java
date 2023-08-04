package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerItTest {
    private static final String URL = "/users";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    User userIn;
    User userOut;
    long userId;

    @BeforeEach
    void setUp() {
        userIn = User.builder()
                .name("name")
                .email("name@mail.ru")
                .build();
        userOut = User.builder()
                .id(1)
                .name("name")
                .email("name@mail.ru")
                .build();
        userId = 1;
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        List<User> userList = List.of(userOut);

        when(userService.getAllUsers()).thenReturn(userList);

        mvc.perform(get(URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userList)));

        verify(userService).getAllUsers();
    }

    @SneakyThrows
    @Test
    void findById() {
        when(userService.getUserById(userId)).thenReturn(userOut);

        mvc.perform(get(URL + "/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOut)));

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void shouldSave() {
        when(userService.saveUser(userIn)).thenReturn(userOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOut)));

        verify(userService).saveUser(userIn);
    }

    @SneakyThrows
    @Test
    void shouldSave_whenNameIsEmpty_fail() {
        userIn.setName("");

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldSave_whenMailIsNotValid_fail() {
        userIn.setEmail("");

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(userIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void shouldUpdate() {
        when(userService.updateUser(userIn, userId)).thenReturn(userOut);

        mvc.perform(patch(URL + "/{id}", userId)
                        .content(mapper.writeValueAsString(userIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(userOut)));

        verify(userService).updateUser(userIn, userId);
    }

    @SneakyThrows
    @Test
    void shouldDelete() {
        when(userService.updateUser(userIn, userId)).thenReturn(userOut);

        mvc.perform(delete(URL + "/{id}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}
