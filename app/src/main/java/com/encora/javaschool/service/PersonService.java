package com.encora.javaschool.service;

import com.encora.javaschool.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonService {

    private final List<Person> personList;

    public PersonService() {
        personList = new ArrayList<>();
        personList.add(new Person("Oswaldo", 25, "ASDASDASDAS"));
        personList.add(new Person("Carlos", 23, "ASDASKJJSJSJSAH"));
        personList.add(new Person("Erick", 24, "XZCZXCZCZXCZX"));
    }

    public List<Person> get() {
        return personList;
    }

    public Person get(String curp) {
        Optional<Person> optionalPerson = personList.stream().filter(person -> curp.equals(person.getCurp())).findFirst();
        return optionalPerson.orElse(null);
    }

    public void save(Person person) {
        personList.add(person);
    }

    public void delete(String curp) {
        personList.removeIf(person -> curp.equals(person.getCurp()));
    }

}
