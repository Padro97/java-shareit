package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE (LOWER(it.name) LIKE LOWER(concat('%', ?1, '%')) " +
            "OR LOWER(it.description) LIKE LOWER(concat('%', ?1, '%'))) AND it.available = true")
    List<Item> search(String text);
}
