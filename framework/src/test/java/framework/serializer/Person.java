package framework.serializer;

public class Person {

    private String name;
    private int age;
    private String curp;
    private boolean superPowerful;
    private float height;
    private double weight;

    public Person() {}

    public Person(String name, int age, String curp, boolean superPowerful, float height, double weight) {
        this.name = name;
        this.age = age;
        this.curp = curp;
        this.superPowerful = superPowerful;
        this.height = height;
        this.weight = weight;
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

    public boolean isSuperPowerful() {
        return superPowerful;
    }

    public void setSuperPowerful(boolean superPowerful) {
        this.superPowerful = superPowerful;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
