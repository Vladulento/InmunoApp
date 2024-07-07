package com.uc3m.InmunoApp.PatientsList;

import java.io.Serializable;

public class Patient implements Serializable {
    private String name;
    private String gender;
    private int age;
    private double height;
    private double weight;

    public Patient(String name, String gender, int age, double height, double weight) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }
    public String getGender() {
        return gender;
    }
    public int getAge() {
        return age;
    }
    public double getHeight() {
        return height;
    }
    public double getWeight() {
        return weight;
    }
}
