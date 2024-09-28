package fr.focusflow.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ROLE")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }
}
