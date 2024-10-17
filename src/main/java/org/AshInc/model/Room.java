package org.AshInc.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.AshInc.timer.Timer;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomName;
    @Transient
    private String chatterLogin;
    @Transient
    private Timer timer;
    private Integer slots = 0;
    private String password;
    private String expirationTime;

    @OneToMany(mappedBy = "room")
    @JsonManagedReference
    private List<Message> messages = new ArrayList<>();
    @ManyToMany(mappedBy = "rooms")
    private List<Chatter> chatters = new ArrayList<>();

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        sb.append(id).append('\n');
        sb.append(roomName).append('\n');
        sb.append(chatterLogin).append('\n');
        sb.append(slots).append('\n');
        sb.append("Password: "+password).append('\n');
        sb.append(expirationTime).append('\n');
        sb.append("Messages: { \n");
        for (Message message: messages){
            sb.append(message.getChatterLogin())
                    .append("|")
                    .append(message.getText())
                    .append("|")
                    .append(message.getSendingTime())
                    .append(" }\n");
        }
        sb.append("Chatters: { \n");
        for (Chatter chatter: chatters){
            sb.append(chatter.getId())
                    .append('|')
                    .append(chatter.getLogin());
        }
        sb.append(" }");

        return sb.toString();
    }
}
