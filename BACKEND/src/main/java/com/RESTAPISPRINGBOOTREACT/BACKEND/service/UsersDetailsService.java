package com.RESTAPISPRINGBOOTREACT.BACKEND.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.RESTAPISPRINGBOOTREACT.BACKEND.repository.UsersRepository;

@Service
public class UsersDetailsService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
    return usersRepository.findByEmail(username).orElseThrow();
    }
}
