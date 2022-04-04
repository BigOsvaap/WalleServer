package com.encora.javaschool;

import framework.Walle;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Walle.run(App.class, args);
    }


}
