package org.AshInc.service;

import org.AshInc.model.Chatter;
import org.AshInc.model.Message;
import org.AshInc.model.Room;
import org.AshInc.repository.MessageRepository;
import org.AshInc.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service // Indicates that this class is a service component in the Spring context
public class RoomService {

    @Autowired // Automatically injects RoomRepository dependency
    private RoomRepository roomRepository;

    @Autowired // Automatically injects MessageRepository dependency
    MessageRepository messageRepository;

    // Retrieves a list of all rooms
    public List<Room> getAllRooms() {
        return roomRepository.findAll(); // Fetches all rooms from the repository
    }

    // Saves a new room to the repository
    public void addNewRoom(Room room) {
        roomRepository.save(room);
    }

    // Updates an existing room with new details
    public void updateRoom(Room newRoom) {
        Room oldRoom = roomRepository.findById(newRoom.getId()).orElse(null); // Find the existing room
        if (oldRoom != null) {
            newRoom.setId(oldRoom.getId()); // Preserve the ID of the old room
            roomRepository.save(newRoom); // Save the updated room
        }
    }

    // Finds a room by its chat name
    public Room findByChatName(String roomName) {
        return roomRepository.findRoomByRoomName(roomName); // Fetches the room by name from the repository
    }

    // Increments the count of taken slots in the room
    public void incrementTakenSlots(Room room) {
        room.setSlots(room.getSlots() + 1);
    }

    // Decrements the count of taken slots in the room
    public void decrementTakenSlots(Room room) {
        room.setSlots(room.getSlots() - 1);
    }

    // Deletes a room by its name, handling associated chatters and messages
    @Transactional // Ensures the method runs within a transaction context
    public void deleteRoomByName(String roomName) {
        Room room = roomRepository.findRoomByRoomName(roomName); // Find the room by name
        if (room != null) { // Check if the room exists
            // Remove this room from all associated chatters
            for (Chatter chatter : room.getChatters()) {
                chatter.getRooms().remove(room);
            }
            // Delete all messages associated with the room
            if (!room.getMessages().isEmpty()) {
                for (Message message : room.getMessages()) {
                    messageRepository.delete(message); // Delete each message
                }
            }
            roomRepository.delete(room); // Finally, delete the room
        }
    }
}
