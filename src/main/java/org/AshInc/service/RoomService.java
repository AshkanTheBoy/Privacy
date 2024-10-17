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

@Service
public class RoomService {
   @Autowired
   private RoomRepository roomRepository;

   @Autowired
    MessageRepository messageRepository;

   public List<Room> getAllRooms(){
       return roomRepository.findAll();
   }

   public void addNewRoom(Room room){
       roomRepository.save(room);
   }

   public void updateRoom(Room newRoom){
       Room oldRoom = roomRepository.findById(newRoom.getId()).orElse(null);
       newRoom.setId(oldRoom.getId());
       roomRepository.save(newRoom);
   }

   public Room findByChatName(String roomName){
       return roomRepository.findRoomByRoomName(roomName);
//       return getAllRooms().stream().filter(room->room.getRoomName().equals(roomName)).findFirst().orElse(null);
   }

   public void incrementTakenSlots(Room room){
       room.setSlots(room.getSlots()+1);
   }

    public void decrementTakenSlots(Room room){
        room.setSlots(room.getSlots()-1);
    }

   @Transactional
   public void deleteRoomByName(String roomName){
       Room room = roomRepository.findRoomByRoomName(roomName);
       if (room!=null){
           for (Chatter chatter: room.getChatters()){
               chatter.getRooms().remove(room);
           }
           if (!room.getMessages().isEmpty()){
               for (Message message: room.getMessages()){
                   messageRepository.delete(message);
               }
           }
           roomRepository.delete(room);
       }
   }
}
