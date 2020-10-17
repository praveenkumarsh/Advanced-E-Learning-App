package com.praveen.learningapp.DashBoard_Module;

public class dashboarditem {
    String testname;
    String testscore;

    public dashboarditem(String testname, String testscore) {
        this.testname = testname;
        this.testscore = testscore;
    }

    public String getTestname() {
        return testname;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }

    public String getTestscore() {
        return testscore;
    }

    public void setTestscore(String testscore) {
        this.testscore = testscore;
    }
}
