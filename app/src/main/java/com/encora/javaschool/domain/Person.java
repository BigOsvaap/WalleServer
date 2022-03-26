package com.encora.javaschool.domain;

public class Person {

    private String name;
    private int age;
    private String curp;

    public Person() {

    }

    public Person(String name, int age, String curp) {
        this.name = name;
        this.age = age;
        this.curp = curp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }
}
