package org.AshInc.service;

import org.AshInc.model.Message;
import org.AshInc.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public List<Message> getLastMessagesByRoomName(String roomName){
        List<Message> ms = messageRepository.findAllByRoomName(roomName,Sort.by("id").descending());
        System.out.println("SERVICE=>");
        System.out.println(ms);
        return ms;
    }

}
