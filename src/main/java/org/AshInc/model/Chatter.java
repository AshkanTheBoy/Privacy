package org.AshInc.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Chatter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String login;
    private String passwordHash;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "chatter_room",
            joinColumns = @JoinColumn(name = "chatter_id"),
            inverseJoinColumns = @JoinColumn(name="room_id")
    )
    private List<Room> rooms = new ArrayList<>();

    public String toString(){
        return String.format("Chatter: [ %d%n %s%n %s%n %d%n]",id,login,passwordHash,rooms.size());
    }
}
