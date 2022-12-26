package client.entity.user;

import org.springframework.security.core.GrantedAuthority;

/**
 * Роли пользователей
 * LOCKED - пользователь был заблокирован
 * USER - стандартная роль для пользователя
 * ADMIN - пользователь с расширенными правами
 * @see User
 * */
public enum Roles implements GrantedAuthority {
    
    USER, ADMIN, LOCKED;
    
    public String getAuthority() {
        
        return name();
    }
}
