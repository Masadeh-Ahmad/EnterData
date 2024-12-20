package com.example.enterdata;

import java.io.Serializable;

public class Data {
    private int id;
    private int grade;

    public Data(){}
    public Data(int grade) {
        this.grade = grade;
    }

    public int getId() {
        return id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}