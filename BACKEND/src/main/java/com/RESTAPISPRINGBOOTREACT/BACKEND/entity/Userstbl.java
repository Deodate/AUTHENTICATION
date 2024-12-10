package com.RESTAPISPRINGBOOTREACT.BACKEND.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "userstbl")
@Data
public class Userstbl implements UserDetails {
    
    @SuppressWarnings("deprecation")
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String password;
    private String nationality;
    private String position;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       
        return List.of(new SimpleGrantedAuthority(position));
    }
    @Override
    public String getUsername() {
       
        return email;
    }

    @Override
    public boolean isAccountNonExpired(){
         return true;
    }
    
    @Override
    public boolean isAccountNonLocked(){
          return true;
    }

    @Override
    public boolean isCredentialsNonExpired(){
         return true;
    }

    @Override
    public boolean isEnabled(){
        return true;
    }
}
