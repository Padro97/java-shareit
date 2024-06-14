package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId);

    @Query("SELECT req FROM ItemRequest req WHERE req.requester.id <> :userId ORDER BY req.created DESC")
    Page<ItemRequest> getAllRequests(@Param("userId") Long userId, Pageable pageable);
}

