package com.praveen.learningapp.Test_Module;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class TestList implements Serializable {

    private String name;
    private ArrayList<TestQuestionAnswer> testQuestionAnswer;

    public TestList(){

    }

    public TestList(String name, ArrayList<TestQuestionAnswer> testQuestionAnswer) {
        this.name = name;
        this.testQuestionAnswer = testQuestionAnswer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<TestQuestionAnswer> getTestQuestionAnswer() {
        return testQuestionAnswer;
    }

    public void setTestQuestionAnswer(ArrayList<TestQuestionAnswer> testQuestionAnswer) {
        this.testQuestionAnswer = testQuestionAnswer;
    }

    @NonNull
    @Override
    public String toString() {
        super.toString();
        return name+"-"+testQuestionAnswer.toString();
    }
}
