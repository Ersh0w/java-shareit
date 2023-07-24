package ru.practicum.shareit.request;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import javax.persistence.*;

@Entity
@Table(name = "requests")
@Data
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User requestor;
}