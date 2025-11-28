package com.hackathon.resolutionconsent.digipin.util;

import java.io.Serial;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hackathon.resolutionconsent.digipin.models.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetailsImplement implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private boolean aadhaarVerified;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImplement(Long id, String username, String password, String email, 
                                String phone, boolean aadhaarVerified,
                                Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.aadhaarVerified = aadhaarVerified;
        this.authorities = authorities;
    }

    public static UserDetailsImplement build(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return new UserDetailsImplement(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                user.getEmailId(),
                user.getPhoneNumber(),
                user.isAadhaarVerified(),
                Collections.singletonList(authority)
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username; 
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
