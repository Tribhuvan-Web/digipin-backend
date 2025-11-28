package com.hackathon.resolutionconsent.digipin.service;

import com.hackathon.resolutionconsent.digipin.models.User;
import com.hackathon.resolutionconsent.digipin.repository.UserRepository;
import com.hackathon.resolutionconsent.digipin.util.UserDetailsImplement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userRepository.findByEmailId(username);
        
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhoneNumber(username);
        }

        User user = userOpt.orElseThrow(() -> 
            new UsernameNotFoundException("User not found with username: " + username));

        return UserDetailsImplement.build(user);
    }

    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        return UserDetailsImplement.build(user);
    }
}
