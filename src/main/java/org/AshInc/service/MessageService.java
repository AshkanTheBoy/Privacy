package org.AshInc.service;

import org.AshInc.model.Message;
import org.AshInc.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

}
