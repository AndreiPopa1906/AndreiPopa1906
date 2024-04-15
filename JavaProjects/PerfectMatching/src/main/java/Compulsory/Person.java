package Compulsory;

import java.util.*;
public abstract class Person {
    /**
     * Name of the person
     */
    private String name;
    /**
     * Age of the person
     */
    private int age;
    /**
     * Destination of the person
     */
    private Destination destination;

    private Destination startingPoint;

    public Person(String name, int age, Destination startingPoint, Destination destination) {
        this.name = name;
        this.age = age;
        this.startingPoint = startingPoint;
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Destination getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Destination startingPoint) {
        this.startingPoint = startingPoint;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", startingPoint=" + startingPoint.getName() +
                ", destination=" + destination.getName() +
                '}';
    }
}
