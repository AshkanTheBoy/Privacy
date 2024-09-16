package org.AshInc.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String sendingTime;
    private String chatterLogin;
    private String roomName;
    @ManyToOne
    @JoinColumn(name = "room_id")
    @JsonBackReference
    private Room room;

    public String toString(){
        return String.format("Message: [ %d%n %s%n %s%n %s%n %s%n %s%n]",id,text,sendingTime,chatterLogin,roomName,room.getRoomName());
    }
}
