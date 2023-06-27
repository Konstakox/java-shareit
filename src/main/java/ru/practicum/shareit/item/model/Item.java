package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.persistence.*;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @Column(name = "item_id", nullable = false, length = Integer.MAX_VALUE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "item_name", nullable = false, length = 64)
    private String name;

    @Column(nullable = false, length = 256)
    private String description;

    @Column(nullable = false)
    private Boolean available = true;

//    @ManyToOne
//    @JoinColumn(name = "owner", nullable = false)
    @Column(name = "owner", nullable = false)
    private Integer owner;

    @ManyToOne
    @JoinColumn(name = "request")
    private ItemRequest request;
}
