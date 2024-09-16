package org.AshInc.service;

import org.AshInc.model.Chatter;
import org.AshInc.repository.ChatterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatterService {
    @Autowired
    ChatterRepository chatterRepository;
    public List<Chatter> findAll(){
        return chatterRepository.findAll();
    }

    public Chatter findById(Long id){
        return chatterRepository.findById(id).orElse(null);
    }

    @Transactional
    public void addNewUser(Chatter user){
        chatterRepository.save(user);
    }

    public Chatter findUserByLogin(String login){
        List<Chatter> chatters = findAll();
        for (Chatter chatter: chatters){
            if (chatter.getLogin().equals(login)){
                return chatter;
            }
        }
        return null;
//       return findAll().stream().filter(user->user.getLogin().equals(login)).findFirst().orElse(null);
    }
}
