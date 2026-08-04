package com.example.oauthserver.security;

import com.example.oauthserver.entity.User;
import com.example.oauthserver.repository.FileUserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final FileUserRepository userRepository;

    public CustomUserDetailsService(FileUserRepository userRepository) {this.userRepository = userRepository; }
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole()))); }
}