package org.AshInc.service;

import org.AshInc.model.Message;
import org.AshInc.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Transactional
    public void saveMessage(Message message){
        messageRepository.save(message);
    }

    public void remove(Message message){
        messageRepository.delete(message);
    }

    public Page<Message> getLastMessagesByRoomName(String roomName){
        Pageable pageable = PageRequest.of(0,5,Sort.by("id").descending());
        Page<Message> ms = messageRepository.findAllByRoomName(roomName,pageable);
        return ms;
    }

}
