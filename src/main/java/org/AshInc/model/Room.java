package org.AshInc.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AshInc.timer.Timer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data // Generates getters, setters, equals, hashCode, and toString methods
@AllArgsConstructor // Generates a constructor with all parameters
@NoArgsConstructor // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
@Table // Specifies the table name (default is the class name)
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
) // Handles serialization of references to prevent infinite recursion
public class Room {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the value for the primary key
    private Long id; // Unique identifier for the Room

    private String roomName; // Name of the room

    @Transient // Indicates that this field is not persisted in the database
    private Timer timer; // Timer associated with the room (not stored)

    private Integer slots = 0; // Number of currently occupied slots in the room
    private String password; // Password to access the room
    private String expirationTime; // Expiration time for the room (if applicable)

    @OneToMany(mappedBy = "room") // Defines a one-to-many relationship with Message
    @JsonManagedReference // Manages the forward part of the relationship for serialization
    private List<Message> messages = new ArrayList<>(); // List of messages in the room

    @ManyToMany(mappedBy = "rooms") // Defines a many-to-many relationship with Chatter
    private List<Chatter> chatters = new ArrayList<>(); // List of chatters in the room

    @Override
    public String toString() {
        // Custom toString method for easy logging and debugging
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append(id).append('\n');
        sb.append(roomName).append('\n');
        sb.append(slots).append('\n');
        sb.append("Password: ").append(password).append('\n');
        sb.append(expirationTime).append('\n');
        sb.append("Messages: { \n");
        for (Message message : messages) {
            sb.append(message.getChatterLogin())
                    .append("|")
                    .append(message.getText())
                    .append("|")
                    .append(message.getSendingTime())
                    .append(" }\n");
        }
        sb.append("Chatters: { \n");
        for (Chatter chatter : chatters) {
            sb.append(chatter.getId())
                    .append('|')
                    .append(chatter.getLogin());
        }
        sb.append(" }");

        return sb.toString(); // Return the constructed string representation
    }
}
