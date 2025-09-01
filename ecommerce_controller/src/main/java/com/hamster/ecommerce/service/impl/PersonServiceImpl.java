package com.hamster.ecommerce.service.impl;

import com.hamster.ecommerce.exception.AccessDeniedException;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.repository.PersonRepository;
import com.hamster.ecommerce.service.PersonService;
import com.hamster.ecommerce.util.ContextUtil;
import org.springframework.stereotype.Service;


@Service
public class PersonServiceImpl implements PersonService
{

    private final ContextUtil contextUtil;
    private final PersonRepository personRepository;

    public PersonServiceImpl(ContextUtil contextUtil, PersonRepository personRepository)
    {
        this.contextUtil = contextUtil;
        this.personRepository = personRepository;
    }

    @Override
    public Person getPersonByCurrentUser()
    {
        Long userId = this.contextUtil.getCurrentUserId();

        if (userId < 0)
        {
            throw new AccessDeniedException();
        }

        return personRepository.findByLoginId(userId).orElse(new Person(userId));
    }

    @Override
    public Person savePerson(Person person)
    {
        return personRepository.save(person);
    }
}
