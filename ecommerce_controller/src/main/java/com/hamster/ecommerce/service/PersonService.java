package com.hamster.ecommerce.service;

import com.hamster.ecommerce.model.entity.Person;

public interface PersonService
{
    Person getPersonByCurrentUser();

    Person savePerson(Person person);
}
