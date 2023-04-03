package ru.vitasoft.testWork.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Role {

    USER("Пользователь", Set.of(Permission.USER_PERMISSION)),
    OPERATOR("Оператор", Set.of(Permission.OPERATOR_PERMISSION)),
    ADMIN("Администратор", Set.of(Permission.ADMIN_PERMISSION));

    @Getter
    private final String roleName;
    private final Set<Permission> permissions;

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
    }

}
