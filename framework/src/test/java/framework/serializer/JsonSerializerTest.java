package framework.serializer;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonSerializerTest {

    @Test
    void serializer_withPersonObject() throws IllegalAccessException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        Person person = new Person("Oswaldo", 25, "ASDKAHSDKAS", false, 1.76f, 110.5);

        String json = JsonSerializer.serialize(person);
        Person object = JsonSerializer.deserialize(json, Person.class);

        assertEquals(person.getName(), object.getName());
        assertEquals(person.getAge(), object.getAge());
        assertEquals(person.getCurp(), object.getCurp());
        assertEquals(person.isSuperPowerful(), object.isSuperPowerful());
        assertEquals(person.getHeight(), object.getHeight());
        assertEquals(person.getWeight(), object.getWeight());
    }

    @Test
    void serializer_withListOfPeople() throws IllegalAccessException {
        List<Person> list = new LinkedList<>();

        String emptyListJson = JsonSerializer.serializeList(list);

        list.add(new Person("Oswaldo", 25, "ASDKAHSDKAS", false, 1.76f, 110.5));
        list.add(new Person("Oswaldo", 25, "ASDKAHSDKAS", false, 1.76f, 110.5));

        String listJson = JsonSerializer.serializeList(list);

        assertEquals(emptyListJson, "[]");
        assertEquals(listJson, String.format("[%s,%s]", JsonSerializer.serialize(list.get(0)), JsonSerializer.serialize(list.get(0))));
    }

}