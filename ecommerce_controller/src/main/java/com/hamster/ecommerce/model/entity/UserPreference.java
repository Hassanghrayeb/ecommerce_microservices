package com.hamster.ecommerce.model.entity;

import com.hamster.ecommerce.model.enums.InterfaceTheme;
import com.hamster.ecommerce.model.enums.Language;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_preference")
public class UserPreference
{
    @Id
    private Long id;
    private InterfaceTheme theme;
    private Language language;
    private Long loginId;

    public static UserPreference getDefaultUserPreference()
    {
        UserPreference userPreference = new UserPreference();
        userPreference.setTheme(InterfaceTheme.LIGHT);
        userPreference.setLanguage(Language.ENGLISH);
        return userPreference;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public InterfaceTheme getTheme()
    {
        return theme;
    }

    public void setTheme(InterfaceTheme theme)
    {
        this.theme = theme;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    public Long getLoginId()
    {
        return loginId;
    }

    public void setLoginId(Long loginId)
    {
        this.loginId = loginId;
    }
}
