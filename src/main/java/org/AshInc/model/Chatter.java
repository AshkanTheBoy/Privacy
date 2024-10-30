package org.AshInc.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data // Generates getters, setters, equals, hashCode, and toString methods
@AllArgsConstructor // Generates a constructor with all parameters
@NoArgsConstructor // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
@Table // Specifies the table name (default is the class name)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id" // Uses the 'id' field for identity
)
public class Chatter {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the value for the primary key
    private Long id; // Unique identifier for the Chatter

    private String login; // Login name for the Chatter
    private String passwordHash; // Hashed password for security
    private String role = "USER";

    @ManyToMany(cascade = CascadeType.ALL) // Defines a many-to-many relationship with Room
    @JoinTable(
            name = "chatter_room", // Name of the join table
            joinColumns = @JoinColumn(name = "chatter_id"), // Foreign key for this entity
            inverseJoinColumns = @JoinColumn(name="room_id") // Foreign key for the related entity
    )
    private List<Room> rooms = new ArrayList<>(); // List of rooms the Chatter is part of

    @Override
    public String toString() {
        // Custom toString method to prevent stack overflow
        return String.format("Chatter: [ %d%n %s%n %s%n %s%n %d%n]", id, login, passwordHash, role, rooms.size());
    }
}
