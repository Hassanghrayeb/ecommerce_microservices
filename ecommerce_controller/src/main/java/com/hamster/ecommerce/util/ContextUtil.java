package com.hamster.ecommerce.util;


import com.hamster.ecommerce.model.entity.Login;
import com.hamster.ecommerce.model.entity.Role;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Component
public class ContextUtil
{
    public Long getCurrentUserId()
    {
        /*------------------------------------------------------------------+
        |   There should always be a User in the security context.  But, if |
        |   for some reason there is not, simply catch the exception and    |
        |   put an entry of -1 for the change user.                         |
        +------------------------------------------------------------------*/
        try
        {
            return ((Login) getContext().getAuthentication().getPrincipal()).getId();
        }
        catch (NullPointerException | ClassCastException e)
        {
            return -1L;
        }
    }

    public String getCurrentUsername()
    {
        try
        {
            return ((Login) getContext().getAuthentication().getPrincipal()).getUsername();
        }
        catch (NullPointerException | ClassCastException e)
        {
            return null;
        }
    }

    public String getCurrentAccessToken()
    {
        try
        {
            return (String) getContext().getAuthentication().getCredentials();
        }
        catch (NullPointerException | ClassCastException e)
        {
            return null;
        }
    }

    public Login getCurrentUser()
    {
        try
        {
            return ((Login) getContext().getAuthentication().getPrincipal());
        }
        catch (NullPointerException | ClassCastException e)
        {
            return null;
        }
    }

    public List<Role> getCurrentUserRoles()
    {
        try
        {
            return ((Login) getContext().getAuthentication().getPrincipal()).getRoles();
        }
        catch (NullPointerException | ClassCastException e)
        {
            return null;
        }
    }
}
