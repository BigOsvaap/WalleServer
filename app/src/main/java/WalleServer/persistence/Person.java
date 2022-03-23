package WalleServer.persistence;

import java.util.Arrays;

public class Person {

    private String name;
    private String lastName;
    private Integer age;
    private String curp;

    public Person() {}

    public Person(String name, String lastName, Integer age, String curp) {
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.curp = curp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":" + "\"" + name + "\"," +
                "\"lastName\":" + "\"" + lastName + "\"," +
                "\"age\":" + age + "," +
                "\"curp\":" + "\"" + curp + "\"" +
                "}";
    }

    public static Person JSONToPerson(String json) {
        var person = new Person();
        json = json.replace("{", "");
        json = json.replace("}", "");
        var keyValues = json.split(",");
        Arrays.stream(keyValues).forEach(keyValue -> {
            var field = keyValue.split(":");
            var keyField = field[0].trim().strip().replace("\"", "");
            var valueField = field[1].trim().strip().replace("\"", "");
            switch (keyField) {
                case "name" -> person.setName(valueField);
                case "lastName" -> person.setLastName(valueField);
                case "age" -> person.setAge(Integer.parseInt(valueField));
                case "curp" -> person.setCurp(valueField);
            }
        });
        return person;
    }
}
