package org.AshInc.security;

import org.AshInc.model.Chatter;
import org.AshInc.repository.ChatterRepository;
import org.AshInc.service.ChatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service  // Marks this class as a service component for Spring
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ChatterService chatterService;  // Injects the Chatter service for business logic

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        // Loads user details by username (login)
        Chatter chatter = chatterService.findUserByLogin(login);  // Attempts to find the Chatter by login
        if (chatter == null) {
            // Throws an exception if the Chatter is not found
            throw new UsernameNotFoundException("Could not find chatter");
        }

        // Returns a ChatterDetails object representing the authenticated user
        return new ChatterDetails(chatter);
    }
}
