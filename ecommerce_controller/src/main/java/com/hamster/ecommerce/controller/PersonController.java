package com.hamster.ecommerce.controller;

import com.hamster.ecommerce.mapper.PersonMapper;
import com.hamster.ecommerce.model.dto.PersonDTO;
import com.hamster.ecommerce.model.entity.Person;
import com.hamster.ecommerce.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Person Controller")
@RestController
@RequestMapping("/person")
public class PersonController
{

    private final PersonService personService;
    private final PersonMapper personMapper;

    public PersonController(PersonService personService, PersonMapper personMapper)
    {
        this.personService = personService;
        this.personMapper = personMapper;
    }

    @Operation(summary = "Get person info")
    @GetMapping()
    public ResponseEntity<PersonDTO> getPerson()
    {
        Person person = personService.getPersonByCurrentUser();
        return ResponseEntity.ok(personMapper.entityToDTO(person));
    }

    @Operation(summary = "Edit person info")
    @PostMapping
    public ResponseEntity<PersonDTO> savePerson(@RequestBody PersonDTO personDTO)
    {
        Person person = personService.getPersonByCurrentUser();
        person = personMapper.dtoToEntity(personDTO, person);
        person.setStatusId(1L); // Active for now

        personService.savePerson(person);
        return ResponseEntity.ok(personMapper.entityToDTO(person));
    }
}
