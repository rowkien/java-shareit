package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start < ?4 order by b.start desc")
    List<Booking> findLastBookingByOwnerId(int itemId, int bookerId, BookingStatus bookingStatus, LocalDateTime time);

    @Query(value = "select b from Booking b where b.item.id = ?1 and b.item.owner.id = ?2 and b.status <> ?3 and b.start > ?4 order by b.start")
    List<Booking> findNextBookingByOwnerId(int itemId, int bookerId, BookingStatus bookingStatus, LocalDateTime time);

    boolean existsBookingByBookerIdAndItemIdAndStatusAndStartBefore(int userId, int itemId, BookingStatus bookingStatus, LocalDateTime time);
}
