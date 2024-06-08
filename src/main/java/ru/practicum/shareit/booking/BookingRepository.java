package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByBooker_IdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime now);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Long ownerId, LocalDateTime now, LocalDateTime now2);

    List<Booking> findAllByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_Owner_IdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime now);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    List<Booking> findAllByItem_idAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findAllByItem_IdAndStartIsBeforeOrderByStartDesc(Long itemId, LocalDateTime now);

    Boolean existsByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Long itemId, Long userId, LocalDateTime now, BookingStatus status);

}
