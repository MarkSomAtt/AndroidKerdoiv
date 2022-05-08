package com.example.andoidkerdoiv;

public class Questions {
    private String question;
    private float rated;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
    private String id;
    private int kitoltesCount;

    public Questions() {
    }

    public Questions(String question, float rated, String answer1, String answer2, String answer3, String answer4, String answer5,int kitoltesCount) {
        this.question = question;
        this.rated = rated;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.answer5 = answer5;
        this.kitoltesCount=kitoltesCount;
    }

    public String getQuestion() {
        return question;
    }

    public float getRated() {
        return rated;
    }

    public String getAnswer1() {
        return answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public String getAnswer5() {
        return answer5;
    }
    public int getKitoltesCount(){return kitoltesCount;}

    public String _getId(){return id; }
    public void setId(String str){
        this.id=str;
    }
}
