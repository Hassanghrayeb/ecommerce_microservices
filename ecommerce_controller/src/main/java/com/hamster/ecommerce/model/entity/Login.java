package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table("login")
public class Login extends Auditable implements UserDetails
{
    @Id
    private Long id = null;

    @Column("username")
    private String username;

    private String password;

    private Boolean enabled;

    @Transient
    private List<Role> roles = new ArrayList<>();

    @Transient
    private Person person;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Override
    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public Boolean getEnabled()
    {
        return enabled;
    }

    public List<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(List<Role> roles)
    {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        Set<GrantedAuthority> gaSet = new HashSet<>();
        roles.stream().map(Role::getPermissions).forEach(gaSet::addAll);

        return gaSet;
    }

    public boolean hasPermission(@NonNull String permissionName)
    {
        return roles.stream().flatMap(role -> role.getPermissions().stream())
                .anyMatch(permission -> permissionName.equalsIgnoreCase(permission.getName()));
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return !enabled;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return !enabled;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return !enabled;
    }

    public Person getPerson()
    {
        return person;
    }

    public void setPerson(Person person)
    {
        this.person = person;
    }
}
