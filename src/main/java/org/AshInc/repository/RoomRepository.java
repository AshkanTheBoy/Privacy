package org.AshInc.repository;

import org.AshInc.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room,Long> {
    @Query("SELECT r FROM Room r WHERE r.roomName=:roomName")
    Room findRoomByRoomName(@Param("roomName") String roomName);
}
