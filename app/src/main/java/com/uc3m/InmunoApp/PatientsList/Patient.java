package com.uc3m.InmunoApp.PatientsList;

public class Patient {
    private final String name;
    private final String gender;
    private final int age;
    private final int height;
    private final int weight;
    private final Doctor doctor;

    public Patient(String name, String gender, int age, int height, int weight, Doctor doctor) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.doctor = doctor;
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
    public int getHeight() {
        return height;
    }
    public int getWeight() {
        return weight;
    }
    public Doctor getDoctor() {
        return doctor;
    }
}

