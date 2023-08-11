package ru.practicum.shareit.item;

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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerItTest {
    private static final String URL = "/items";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    ItemDto itemDtoIn;
    ItemDto itemDtoOut;
    long userId;

    @BeforeEach
    void setUp() {
        itemDtoIn = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
        itemDtoOut = ItemDto.builder()
                .id(1)
                .name("name")
                .description("description")
                .available(true)
                .build();

        userId = 1;
    }

    @SneakyThrows
    @Test
    void getItemById() {
        when(itemService.getItemById(itemDtoOut.getId(), userId)).thenReturn(itemDtoOut);

        mvc.perform(get(URL + "/" + itemDtoOut.getId())
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)));

        verify(itemService).getItemById(itemDtoOut.getId(), userId);
    }

    @SneakyThrows
    @Test
    void getAllItemsOfUser() {
        when(itemService.getAllItemsOfUser(0, 20, userId)).thenReturn(List.of(itemDtoOut));

        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))));

        verify(itemService).getAllItemsOfUser(0, 20, userId);
    }

    @SneakyThrows
    @Test
    void saveNewItem() {
        when(itemService.saveNewItem(itemDtoIn, userId)).thenReturn(itemDtoOut);

        mvc.perform(post(URL)
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)));

        verify(itemService).saveNewItem(itemDtoIn, userId);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        when(itemService.updateItem(itemDtoIn, itemDtoOut.getId(), userId)).thenReturn(itemDtoOut);

        mvc.perform(patch((URL + "/" + itemDtoOut.getId()))
                        .content(mapper.writeValueAsString(itemDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(itemDtoOut)));

        verify(itemService).updateItem(itemDtoIn, itemDtoOut.getId(), userId);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        when(itemService.searchItems(0, 20, "description")).thenReturn(List.of(itemDtoOut));

        mvc.perform(get(URL + "/search")
                        .param("text", "description")
                        .param("from", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(itemDtoOut))));

        verify(itemService).searchItems(0, 20, "description");
    }
}
