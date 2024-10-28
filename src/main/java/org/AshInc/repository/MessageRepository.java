package org.AshInc.repository;

import org.AshInc.model.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message,Long> {
   List<Message> findAllByRoomName(String roomName, Sort sort);
}
