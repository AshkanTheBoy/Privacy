package org.AshInc.service;

import org.AshInc.model.Room;
import org.AshInc.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoomService {
   @Autowired
   private RoomRepository roomRepository;

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

   public Room findByChatName(String chatName){
       List<Room> rooms = getAllRooms();
       for (Room room: rooms){
           if (room.getRoomName().equals(chatName)){
               return room;
           }
       }
       return null;
//       return getAllRooms().stream().filter(room->room.getRoomName().equals(chatName)).findFirst().orElse(null);
   }

   public void incrementTakenSlots(Room room){
       room.setSlots(room.getSlots()+1);
   }

    public void decrementTakenSlots(Room room){
        room.setSlots(room.getSlots()-1);
    }

   @Transactional
   public void deleteRoomByName(String name){
       Room room = findByChatName(name);
       roomRepository.delete(room);
   }
}
