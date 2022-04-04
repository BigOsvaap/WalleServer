package com.encora.javaschool.controller;

import com.encora.javaschool.domain.Person;
import com.encora.javaschool.service.PersonService;
import framework.http.annotations.Autowired;
import framework.http.annotations.DELETE;
import framework.http.annotations.GET;
import framework.http.annotations.POST;
import framework.http.annotations.PathParam;
import framework.http.annotations.RequestBody;
import framework.http.annotations.RestController;

import java.util.List;

@RestController("/people")
public class PeopleController {

    @Autowired
    private PersonService service;

    @GET
    public List<Person> getPeople() {
        return service.get();
    }

    @GET("/{curp}")
    public Person getPerson(@PathParam("curp") String curp) {
        return service.get(curp);
    }

    @POST
    public void savePerson(@RequestBody Person person) {
        service.save(person);
    }

    @DELETE("/{curp}")
    public void deletePerson(@PathParam("curp") String curp) {
        service.delete(curp);
    }

}
