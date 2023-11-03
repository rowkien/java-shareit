package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingLastAndNextDto {
    private int id;
    private int bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}
