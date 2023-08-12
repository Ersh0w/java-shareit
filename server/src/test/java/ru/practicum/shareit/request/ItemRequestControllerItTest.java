package ru.practicum.shareit.request;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerItTest {
    private static final String URL = "/requests";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService itemRequestService;

    ItemRequest itemRequestIn;
    ItemRequestDto itemRequestOut;
    long userId;

    @BeforeEach
    void setUp() {
        itemRequestIn = ItemRequest.builder()
                .description("test description")
                .build();
        itemRequestOut = ItemRequestDto.builder()
                .id(1)
                .description("test description")
                .build();
        userId = 1;
    }

    @SneakyThrows
    @Test
    void saveNewRequest() {
        when(itemRequestService.saveNewRequest(itemRequestIn, userId)).thenReturn(itemRequestOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemRequestIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestOut)));

        verify(itemRequestService).saveNewRequest(itemRequestIn, userId);
    }

    @SneakyThrows
    @Test
    void getRequestsOfUser() {
        when(itemRequestService.getRequestsOfUser(userId)).thenReturn(List.of(itemRequestOut));

        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestOut))));

        verify(itemRequestService).getRequestsOfUser(userId);
    }

    @SneakyThrows
    @Test
    void getRequestById() {
        when(itemRequestService.getRequestById(itemRequestOut.getId(), userId)).thenReturn(itemRequestOut);

        mvc.perform(get(URL + "/" + userId)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemRequestOut)));

        verify(itemRequestService).getRequestById(itemRequestOut.getId(), userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        when(itemRequestService.getAllRequests(0, 20, userId)).thenReturn(List.of(itemRequestOut));

        mvc.perform(get(URL + "/all")
                        .param("from", "0")
                        .param("size", "20")
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemRequestOut))));

        verify(itemRequestService).getAllRequests(0, 20, userId);
    }
}
