package edu.cuhk.csci3310.musictheoryapp.Question;

public class CheckBoxQuestion extends Question
{
    public static final String CHECK_ANSWER = "check";
    public static final String CROSS_ANSWER = "cross";

    public CheckBoxQuestion(int subNumber, QuestionGroup group,
                            String topic, Description[] descriptions, String[] correctAnswer)
    {
        super(subNumber, group, topic, descriptions, correctAnswer);
    }

    public void acceptVisitor(QuestionVisitor visitor) { visitor.visit(this); }
}
