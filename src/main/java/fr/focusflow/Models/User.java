package fr.focusflow.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "USER")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String userName;

    @Column(unique = true)
    private String email;

    private String password;

    @OneToOne
    private Role role;

    public User() {
    }

    ;


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
