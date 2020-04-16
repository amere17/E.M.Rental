package com.amere.trulse;

public class TFquestion {
    String question;
    String answer;
    public TFquestion(String m_qus,String m_ans){
        this.question = m_qus;
        this.answer = m_ans;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }
}
