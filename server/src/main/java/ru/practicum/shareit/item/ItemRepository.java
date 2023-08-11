package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select i From Item i JOIN FETCH i.owner where i.id = ?1")
    Optional<Item> findById(long itemId);

    @Query("select i From Item i JOIN FETCH i.owner where i.owner.id = ?1 ORDER BY i.id ASC")
    List<Item> findAllByOwnerId(Pageable pageable, long userId);

    Optional<Item> findByIdAndOwnerId(long id, long userId);

    @Query("select i From Item i " +
            "where upper(i.name) like upper('%' || ?1 || '%') " +
            "or upper(i.description) like upper('%' || ?1 || '%') " +
            "and i.available = true")
    Page<Item> searchItems(Pageable pageable, String text);

    @Query("SELECT i FROM Item i JOIN FETCH i.request WHERE i.request.id = ?1")
    List<Item> findAllByRequestId(long requestId);

    @Query("SELECT i FROM Item i JOIN FETCH i.request WHERE i.request.id IN (?1)")
    List<Item> findAllByRequestsIds(List<Long> itemRequestsIds);
}
