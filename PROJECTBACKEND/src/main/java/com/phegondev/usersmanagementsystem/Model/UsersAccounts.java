package com.phegondev.usersmanagementsystem.Model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "signup", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone")
})
@Data
public class UsersAccounts implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    @Column(name = "fullname") 
    private String fullname;

    @Column(unique = true)
    private String phone;

    private String password;

    @Column(name = "nationality") // Corrected column mapping for nationality
    private String nationality;

    @Column(name = "position") // Corrected column mapping for position
    private String position;

    // @OneToOne(mappedBy = "usersAccounts")
    // private ForgotPassword forgotPassword;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(position)); // Use "position" instead of "role"
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
