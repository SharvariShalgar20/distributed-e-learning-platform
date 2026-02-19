package com.Sharvari.elearningPlatform.model;

public class Question {

    private String questionId;
    private String text;
    private String[] options; // 4 options A B C D
    private char correctAnswer;
    private int marks;

    public Question(String questionId, String text, String[] options, char correctAnswer, int marks) {
        this.questionId = questionId;
        this.text = text;
        this.options = options;
        this.correctAnswer = Character.toUpperCase(correctAnswer);
        this.marks = marks;
    }

    public boolean checkAnswer(char answer) {
        return Character.toUpperCase(answer) == correctAnswer;
    }

    public void display(int number) {
        System.out.println("\n  Q" + number + ". " + text);
        char[] labels = {'A', 'B', 'C', 'D'};
        for (int i = 0; i < options.length; i++) {
            System.out.println("     " + labels[i] + ") " + options[i]);
        }
        System.out.println("     [Marks: " + marks + "]");
    }

    public String getQuestionId()  { return questionId; }
    public String getText()        { return text; }
    public String[] getOptions()   { return options; }
    public char getCorrectAnswer() { return correctAnswer; }
    public int getMarks()          { return marks; }


}
