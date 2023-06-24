package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @Column(name = "comments_id", length = Integer.MAX_VALUE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(name = "text", nullable = false, length = 511)
    @Size(min = 2)
    private String text;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    private LocalDateTime created;
}
