package com.fkanban.fkanban.kanbans.columns;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fkanban.fkanban.kanbans.Kanban;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Getter
@Setter
@Table(name = "columns", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"kanban_id", "position"})
})
public class Column {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long position;

    @NotNull
    private String title;

    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Invalid color format. Use HEX format, e.g., #FFFFFF.")
    @jakarta.persistence.Column(length = 7)
    @Value("#FFCDCD")
    private String color;

    @ManyToOne
    @JoinColumn(name = "kanban_id", nullable = false)
    @JsonBackReference
    private Kanban kanban;
}
