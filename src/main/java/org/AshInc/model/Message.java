package org.AshInc.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data // Generates getters, setters, equals, hashCode, and toString methods
@AllArgsConstructor // Generates a constructor with all parameters
@NoArgsConstructor // Generates a no-arguments constructor
@Entity // Indicates that this class is a JPA entity
@Table // Specifies the table name (default is the class name)
public class Message {

    @Id // Marks this field as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-generates the value for the primary key
    private Long id; // Unique identifier for the Message

    private String text; // Content of the message
    private String sendingTime; // Time when the message was sent
    private String chatterLogin; // Login of the Chatter who sent the message
    private String roomName; // Name of the room where the message was sent

    @ManyToOne // Defines a many-to-one relationship with Room
    @JoinColumn(name = "room_id") // Foreign key for the related Room
    @JsonBackReference // Prevents serialization of the back reference to avoid infinite recursion
    private Room room; // The Room object associated with this message

    @Override
    public String toString() {
        // Custom toString method for easy logging and debugging
        return String.format("Message: [ %d%n %s%n %s%n %s%n %s%n %s%n]", id, text, sendingTime, chatterLogin, roomName, room.getRoomName());
    }
}
