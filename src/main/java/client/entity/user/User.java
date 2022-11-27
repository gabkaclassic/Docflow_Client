package client.entity.user;

import client.entity.deserializer.UserDeserializer;
import client.entity.process.Participant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Сущность "Пользователь"
 * */
@Getter
@Setter
@JsonDeserialize(using = UserDeserializer.class)
public class User implements UserDetails {
    
    private Long id;

    @JsonIgnore
    private String password;

    private String username;

    private boolean online;

    @JsonIgnore
    private Participant client;
    
    /**
     * Роли пользователя
     * @see Roles
     * */
    private Set<Roles> roles;
    
    public User() {
        
        roles = new HashSet<>();
        roles.add(Roles.USER);
    }
    
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        return roles;
    }
    
    public String getPassword() {
        
        return password;
    }
    
    public String getUsername() {
        
        return username;
    }
    
    @JsonIgnore
    public boolean isAccountNonExpired() {
        
        return true;
    }
    
    @JsonIgnore
    public boolean isAccountNonLocked() {
        
        return !roles.contains(Roles.LOCKED);
    }
    
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        
        return true;
    }
    
    @JsonIgnore
    public boolean isEnabled() {
        
        return true;
    }
}
