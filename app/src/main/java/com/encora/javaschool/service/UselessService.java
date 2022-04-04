package com.encora.javaschool.service;

import framework.http.annotations.Component;

@Component
public class UselessService {

    private String description;

    public UselessService() {
        description = "I'm just a service for test DI";
    }

}
