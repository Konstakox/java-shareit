package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
public class ItemRequest {
    private Integer id;
    private String description;
    private Integer requestor;
    private LocalDateTime created;
}
