package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i From Item i JOIN FETCH i.owner where i.id = ?1")
    Optional<Item> findById(long itemId);

    @Query("select i From Item i JOIN FETCH i.owner where i.owner.id = ?1 ORDER BY i.id ASC")
    List<Item> findAllByOwnerId(long userId);

    Optional<Item> findByIdAndOwnerId(long id, long userId);

    @Query("select i From Item i " +
            "where upper(i.name) like upper('%' || ?1 || '%') "+
            "or upper(i.description) like upper('%' || ?1 || '%') " +
            "and i.available = true")
    List<Item> searchItems(String text);
}
