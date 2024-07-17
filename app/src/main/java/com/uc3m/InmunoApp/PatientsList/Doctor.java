package com.uc3m.InmunoApp.PatientsList;

public class Doctor {
    private final String name;
    private final String mail;
    private final int phone;

    public Doctor(String name, String mail, int phone) {
        this.name = name;
        this.mail = mail;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public int getPhone() {
        return phone;
    }
}
