package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Collections;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItemsOfUser(long userId, long from, long size) {
        Map<String, Object> parameters = Map.of("from", from,
                "size", size);

        return get("/?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> saveNewItem(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, long itemId, long userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> searchItems(long from, long size, String text) {
        if (text.isBlank()) {
            return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
        }

        Map<String, Object> parameters = Map.of("from", from,
                "size", size,
                "text", text);

        return get("/search/?text={text}&from={from}&size={size}", null, parameters);
    }

    public ResponseEntity<Object> saveNewComment(long itemId, Comment comment, long userId) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}
