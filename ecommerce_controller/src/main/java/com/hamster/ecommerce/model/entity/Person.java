package com.hamster.ecommerce.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("person")
public class Person
{
    @Id
    private Long id;

    @Column("login_id")
    private Long loginId;

    @Column("status_id")
    private Long statusId;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;

    @Column("email_address")
    private String email;

    public Person()
    {

    }

    public Person(Long loginId)
    {
        this.loginId = loginId;
    }

    public Person(String firstName, String lastName)
    {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getLoginId()
    {
        return loginId;
    }

    public void setLoginId(Long loginId)
    {
        this.loginId = loginId;
    }

    public Long getStatusId()
    {
        return statusId;
    }

    public void setStatusId(Long statusId)
    {
        this.statusId = statusId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

}
